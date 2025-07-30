package com.spreadsheet.service.impl;

import com.spreadsheet.model.dto.SheetDataDto;
import com.spreadsheet.repository.SheetDataRepository;
import com.spreadsheet.repository.SheetRepository;
import com.spreadsheet.repository.entity.Sheet;
import com.spreadsheet.repository.entity.SheetData;
import com.spreadsheet.service.SheetDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SheetDataServiceImpl implements SheetDataService {

    @Autowired
    private SheetDataRepository sheetDataRepository;

    @Autowired
    private SheetRepository sheetRepository;

    @Override
    @Transactional
    public SheetDataDto updateData(SheetDataDto sheetDataDto) {
        // Verify sheet exists
        Sheet sheet = sheetRepository.findById(sheetDataDto.sheetId())
                .orElseThrow(() -> new RuntimeException("Sheet not found with id: " + sheetDataDto.sheetId()));

        // Check if data already exists for this cell
        Optional<SheetData> existingData = sheetDataRepository.findBySheetIdAndRowNumberAndColumnNumber(
                sheetDataDto.sheetId(), 
                sheetDataDto.rowNumber(), 
                sheetDataDto.columnNumber()
        );

        SheetData sheetData;
        if (existingData.isPresent()) {
            // Update existing data
            sheetData = existingData.get();
            sheetData.setCellValue(sheetDataDto.cellValue());
        } else {
            // Create new data
            sheetData = new SheetData();
            sheetData.setSheet(sheet);
            sheetData.setRowNumber(sheetDataDto.rowNumber());
            sheetData.setColumnNumber(sheetDataDto.columnNumber());
            sheetData.setCellValue(sheetDataDto.cellValue());
        }

        SheetData savedSheetData = sheetDataRepository.save(sheetData);

        return new SheetDataDto(
                savedSheetData.getId(),
                savedSheetData.getSheet().getId(),
                savedSheetData.getRowNumber(),
                savedSheetData.getColumnNumber(),
                savedSheetData.getCellValue()
        );
    }
}
