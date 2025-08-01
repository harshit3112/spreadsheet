package com.spreadsheet.service;

import com.spreadsheet.model.dto.CreateSheetRequest;
import com.spreadsheet.model.dto.SheetResponse;

public interface SheetService {
    
    Long createSheet(CreateSheetRequest request);
    
    SheetResponse getSheet(Long id);
}
