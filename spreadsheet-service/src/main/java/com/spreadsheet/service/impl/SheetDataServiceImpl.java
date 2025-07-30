package com.spreadsheet.service.impl;

import com.spreadsheet.model.dto.*;
import com.spreadsheet.model.enums.CellType;
import com.spreadsheet.repository.SheetDataRepository;
import com.spreadsheet.repository.SheetPermissionRepository;
import com.spreadsheet.repository.SheetRepository;
import com.spreadsheet.repository.entity.Sheet;
import com.spreadsheet.repository.entity.SheetData;
import com.spreadsheet.repository.entity.SheetPermission;
import com.spreadsheet.service.CellEvaluator;
import com.spreadsheet.service.SheetDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SheetDataServiceImpl implements SheetDataService {

    @Autowired
    private SheetDataRepository sheetDataRepository;

    @Autowired
    private SheetRepository sheetRepository;
    
    @Autowired
    private SheetPermissionRepository sheetPermissionRepository;
    
    @Autowired
    private CellEvaluator cellEvaluator;

    // Sheet-level locks to prevent concurrent updates
    private final ConcurrentHashMap<Long, ReentrantLock> sheetLocks = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public SheetResponse updateSheet(Long sheetId, UpdateSheetRequest request) {
        log.info("Updating sheet with ID: {} with {} cell updates", sheetId, request.cells().size());
        
        // Get or create lock for this sheet
        ReentrantLock lock = sheetLocks.computeIfAbsent(sheetId, k -> new ReentrantLock());
        
        lock.lock();
        try {
            // Verify sheet exists
            Sheet sheet = sheetRepository.findById(sheetId)
                    .orElseThrow(() -> new RuntimeException("Sheet not found with id: " + sheetId));

            // Get all existing sheet data for evaluation context
            List<SheetData> existingData = sheetDataRepository.findBySheetId(sheetId);
            Map<String, String> sheetDataContext = new HashMap<>();
            
            for (SheetData data : existingData) {
                String cellKey = convertToExcelNotation(data.getRowNumber(), data.getColumnNumber());
                sheetDataContext.put(cellKey, data.getCellValue());
            }

            // Process each cell update
            for (CellUpdate cellUpdate : request.cells()) {
                updateCell(sheet, cellUpdate, sheetDataContext);
            }

            // Re-evaluate all expressions after updates
            reevaluateExpressions(sheetId, sheetDataContext);

            log.info("Successfully updated sheet with ID: {}", sheetId);
            
            // Return updated sheet response
            return buildSheetResponse(sheet);
            
        } finally {
            lock.unlock();
        }
    }
    
    private void updateCell(Sheet sheet, CellUpdate cellUpdate, Map<String, String> sheetDataContext) {
        Optional<SheetData> existingData = sheetDataRepository.findBySheetIdAndRowNumberAndColumnNumber(
                sheet.getId(), 
                cellUpdate.rowNumber(), 
                cellUpdate.columnNumber()
        );

        SheetData sheetData;
        if (existingData.isPresent()) {
            sheetData = existingData.get();
        } else {
            sheetData = new SheetData();
            sheetData.setSheet(sheet);
            sheetData.setRowNumber(cellUpdate.rowNumber());
            sheetData.setColumnNumber(cellUpdate.columnNumber());
        }

        sheetData.setCellType(cellUpdate.cellType());
        
        if (cellUpdate.cellType() == CellType.VALUE) {
            sheetData.setCellValue(cellUpdate.value() != null ? cellUpdate.value().toString() : "");
            sheetData.setExpression(null);
            sheetData.setEvaluatedValue(sheetData.getCellValue());
        } else {
            sheetData.setExpression(cellUpdate.expression());
            sheetData.setCellValue(cellUpdate.expression());
            
            // Evaluate expression
            String evaluatedValue = cellEvaluator.evaluateExpression(cellUpdate.expression(), sheetDataContext);
            sheetData.setEvaluatedValue(evaluatedValue);
        }

        sheetDataRepository.save(sheetData);
        
        // Update context for subsequent evaluations
        String cellKey = convertToExcelNotation(cellUpdate.rowNumber(), cellUpdate.columnNumber());
        sheetDataContext.put(cellKey, sheetData.getEvaluatedValue());
        
        log.debug("Updated cell {}:{} with type: {}, value: {}", 
                cellUpdate.rowNumber(), cellUpdate.columnNumber(), 
                cellUpdate.cellType(), sheetData.getEvaluatedValue());
    }
    
    private void reevaluateExpressions(Long sheetId, Map<String, String> sheetDataContext) {
        List<SheetData> expressionCells = sheetDataRepository.findBySheetId(sheetId)
                .stream()
                .filter(data -> data.getCellType() == CellType.EXPRESSION)
                .collect(Collectors.toList());
        
        boolean hasChanges;
        int maxIterations = 10; // Prevent infinite loops
        int iteration = 0;
        
        do {
            hasChanges = false;
            iteration++;
            
            for (SheetData data : expressionCells) {
                String oldValue = data.getEvaluatedValue();
                String newValue = cellEvaluator.evaluateExpression(data.getExpression(), sheetDataContext);
                
                if (!oldValue.equals(newValue)) {
                    data.setEvaluatedValue(newValue);
                    sheetDataRepository.save(data);
                    
                    // Update context
                    String cellKey = convertToExcelNotation(data.getRowNumber(), data.getColumnNumber());
                    sheetDataContext.put(cellKey, newValue);
                    hasChanges = true;
                    
                    log.debug("Re-evaluated cell {}: {} -> {}", cellKey, oldValue, newValue);
                }
            }
        } while (hasChanges && iteration < maxIterations);
        
        if (iteration >= maxIterations) {
            log.warn("Maximum re-evaluation iterations reached for sheet: {}", sheetId);
        }
    }
    
    private SheetResponse buildSheetResponse(Sheet sheet) {
        // Get sheet data
        List<SheetData> sheetDataList = sheetDataRepository.findBySheetId(sheet.getId());
        Map<String, Cell> sheetDataMap = new HashMap<>();
        
        for (SheetData data : sheetDataList) {
            String cellKey = convertToExcelNotation(data.getRowNumber(), data.getColumnNumber());
            Object value = data.getCellType() == CellType.VALUE ? data.getCellValue() : data.getEvaluatedValue();
            
            Cell cell = new Cell(
                    data.getCellType(),
                    value,
                    data.getExpression()
            );
            sheetDataMap.put(cellKey, cell);
        }
        
        // Get permissions
        List<SheetPermission> permissions = sheetPermissionRepository.findBySheetId(sheet.getId());
        List<UserPermission> userPermissions = permissions.stream()
                .map(p -> new UserPermission(p.getUserId(), p.getPermission()))
                .collect(Collectors.toList());
        
        Long createdAt = sheet.getCreatedAt().toEpochSecond(ZoneOffset.UTC);
        
        return new SheetResponse(
                createdAt,
                sheet.getUserId(),
                sheetDataMap,
                userPermissions
        );
    }
    
    private String convertToExcelNotation(int row, int column) {
        StringBuilder columnName = new StringBuilder();
        while (column > 0) {
            column--;
            columnName.insert(0, (char) ('A' + column % 26));
            column /= 26;
        }
        return columnName.toString() + row;
    }
}
