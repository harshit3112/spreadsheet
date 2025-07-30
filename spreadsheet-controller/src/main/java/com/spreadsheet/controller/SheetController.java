package com.spreadsheet.controller;

import com.spreadsheet.model.dto.ApiResponse;
import com.spreadsheet.model.dto.CreateSheetRequest;
import com.spreadsheet.model.dto.SheetResponse;
import com.spreadsheet.service.SheetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/sheet")
@Tag(name = "Sheet Management", description = "APIs for managing spreadsheets")
@Slf4j
public class SheetController {

    @Autowired
    private SheetService sheetService;

    @PostMapping
    @Operation(summary = "Create a new sheet", description = "Creates a new spreadsheet for the specified user")
    public ResponseEntity<ApiResponse<Long>> createSheet(@Valid @RequestBody CreateSheetRequest request) {
        try {
            Long sheetId = sheetService.createSheet(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Sheet created successfully", sheetId));
        } catch (Exception ex) {
            log.error("Failed to create sheet for user: {}", request.userId(), ex);
            throw new RuntimeException("Failed to create sheet: " + ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get sheet by ID", description = "Retrieves a spreadsheet by its unique identifier")
    public ResponseEntity<ApiResponse<SheetResponse>> getSheet(
            @Parameter(description = "Sheet ID", required = true)
            @PathVariable Long id) {
        try {
            SheetResponse sheet = sheetService.getSheet(id);
            return ResponseEntity.ok(ApiResponse.success("Sheet retrieved successfully", sheet));
        } catch (Exception ex) {
            log.error("Failed to retrieve sheet with ID: {}", id, ex);
            throw new RuntimeException("Failed to retrieve sheet: " + ex.getMessage());
        }
    }
}
