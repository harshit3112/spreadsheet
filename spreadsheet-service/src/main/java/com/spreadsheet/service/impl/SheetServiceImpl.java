package com.spreadsheet.service.impl;

import com.spreadsheet.model.dto.SheetDto;
import com.spreadsheet.repository.SheetRepository;
import com.spreadsheet.repository.entity.Sheet;
import com.spreadsheet.service.SheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SheetServiceImpl implements SheetService {

    @Autowired
    private SheetRepository sheetRepository;

    @Override
    @Transactional
    public SheetDto createSheet(SheetDto sheetDto) {
        Sheet sheet = new Sheet();
        sheet.setName(sheetDto.name());
        sheet.setDescription(sheetDto.description());
        sheet.setRowCount(sheetDto.rowCount());
        sheet.setColumnCount(sheetDto.columnCount());
        
        Sheet savedSheet = sheetRepository.save(sheet);
        
        return new SheetDto(
                savedSheet.getId(),
                savedSheet.getName(),
                savedSheet.getDescription(),
                savedSheet.getRowCount(),
                savedSheet.getColumnCount()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public SheetDto getSheet(Long id) {
        Sheet sheet = sheetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sheet not found with id: " + id));
        
        return new SheetDto(
                sheet.getId(),
                sheet.getName(),
                sheet.getDescription(),
                sheet.getRowCount(),
                sheet.getColumnCount()
        );
    }
}
