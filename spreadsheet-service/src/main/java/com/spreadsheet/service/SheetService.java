package com.spreadsheet.service;

import com.spreadsheet.model.dto.SheetDto;

public interface SheetService {
    
    SheetDto createSheet(SheetDto sheetDto);
    
    SheetDto getSheet(Long id);
}
