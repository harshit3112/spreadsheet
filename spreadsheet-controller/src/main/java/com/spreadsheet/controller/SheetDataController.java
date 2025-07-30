package com.spreadsheet.controller;

import com.spreadsheet.model.dto.ApiResponse;
import com.spreadsheet.model.dto.SheetDataDto;
import com.spreadsheet.service.SheetDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/sheet-data")
@Tag(name = "Sheet Data Management", description = "APIs for managing spreadsheet cell data")
public class SheetDataController {

    @Autowired
    private SheetDataService sheetDataService;

    @PutMapping("/update")
    @Operation(summary = "Update cell data", description = "Updates or creates data for a specific cell in a spreadsheet")
    public ResponseEntity<ApiResponse<SheetDataDto>> updateData(
            @Valid @RequestBody SheetDataDto sheetDataDto) {
        try {
            SheetDataDto updatedData = sheetDataService.updateData(sheetDataDto);
            return ResponseEntity.ok(ApiResponse.success("Cell data updated successfully", updatedData));
        } catch (Exception ex) {
            throw new RuntimeException("Failed to update cell data: " + ex.getMessage());
        }
    }
}
