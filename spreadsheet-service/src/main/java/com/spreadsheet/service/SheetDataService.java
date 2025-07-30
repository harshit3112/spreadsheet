package com.spreadsheet.service;

import com.spreadsheet.model.dto.UpdateSheetRequest;
import com.spreadsheet.model.dto.SheetResponse;

public interface SheetDataService {
    
    SheetResponse updateSheet(Long sheetId, UpdateSheetRequest request);
}
