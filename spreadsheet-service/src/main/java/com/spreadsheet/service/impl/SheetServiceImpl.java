package com.spreadsheet.service.impl;

import com.spreadsheet.model.dto.*;
import com.spreadsheet.model.enums.CellType;
import com.spreadsheet.model.enums.Permission;
import com.spreadsheet.repository.SheetDataRepository;
import com.spreadsheet.repository.SheetPermissionRepository;
import com.spreadsheet.repository.SheetRepository;
import com.spreadsheet.repository.entity.Sheet;
import com.spreadsheet.repository.entity.SheetData;
import com.spreadsheet.repository.entity.SheetPermission;
import com.spreadsheet.service.SheetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SheetServiceImpl implements SheetService {

    @Autowired
    private SheetRepository sheetRepository;
    
    @Autowired
    private SheetDataRepository sheetDataRepository;
    
    @Autowired
    private SheetPermissionRepository sheetPermissionRepository;

    @Override
    @Transactional
    public Long createSheet(CreateSheetRequest request) {
        log.info("Creating new sheet for user: {}", request.userId());
        
        Sheet sheet = new Sheet();
        sheet.setUserId(request.userId());
        
        Sheet savedSheet = sheetRepository.save(sheet);
        
        // Create default permission for the creator
        SheetPermission permission = new SheetPermission();
        permission.setSheet(savedSheet);
        permission.setUserId(request.userId());
        permission.setPermission(Permission.EDIT);
        sheetPermissionRepository.save(permission);
        
        log.info("Created sheet with ID: {} for user: {}", savedSheet.getId(), request.userId());
        return savedSheet.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public SheetResponse getSheet(Long id) {
        log.info("Fetching sheet with ID: {}", id);
        
        Sheet sheet = sheetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sheet not found with id: " + id));
        
        // Get sheet data
        List<SheetData> sheetDataList = sheetDataRepository.findBySheetId(id);
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
        List<SheetPermission> permissions = sheetPermissionRepository.findBySheetId(id);
        List<UserPermission> userPermissions = permissions.stream()
                .map(p -> new UserPermission(p.getUserId(), p.getPermission()))
                .collect(Collectors.toList());
        
        Long createdAt = sheet.getCreatedAt().toEpochSecond(ZoneOffset.UTC);
        
        log.info("Retrieved sheet with ID: {} containing {} cells and {} permissions", 
                id, sheetDataMap.size(), userPermissions.size());
        
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
