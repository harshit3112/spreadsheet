package com.spreadsheet.controller;

import com.spreadsheet.model.dto.ApiResponse;
import com.spreadsheet.model.dto.SheetResponse;
import com.spreadsheet.model.dto.UpdateSheetRequest;
import com.spreadsheet.service.SheetDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/sheet")
@Tag(name = "Sheet Data Management", description = "APIs for managing spreadsheet cell data")
@Slf4j
public class SheetDataController {

    @Autowired
    private SheetDataService sheetDataService;

    @PutMapping("/{id}")
    @Operation(summary = "Update sheet data", description = "Updates multiple cells in a spreadsheet with locking to prevent concurrent updates")
    public ResponseEntity<ApiResponse<SheetResponse>> updateSheet(
            @Parameter(description = "Sheet ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UpdateSheetRequest request) {
        try {
            log.info("Updating sheet with ID: {} with {} cell updates", id, request.cells().size());
            SheetResponse updatedSheet = sheetDataService.updateSheet(id, request);
            log.info("Successfully updated sheet with ID: {} containing {} cells", 
                    id, updatedSheet.sheetData().size());
            return ResponseEntity.ok(ApiResponse.success("Sheet updated successfully", updatedSheet));
        } catch (Exception ex) {
            log.error("Failed to update sheet with ID: {}", id, ex);
            throw new RuntimeException("Failed to update sheet: " + ex.getMessage());
        }
    }
}
