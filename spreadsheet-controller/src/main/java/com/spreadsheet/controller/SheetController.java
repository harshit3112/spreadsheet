package com.spreadsheet.controller;

import com.spreadsheet.model.dto.ApiResponse;
import com.spreadsheet.model.dto.SheetDto;
import com.spreadsheet.service.SheetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/sheet")
@Tag(name = "Sheet Management", description = "APIs for managing spreadsheets")
public class SheetController {

    @Autowired
    private SheetService sheetService;

    @PostMapping("/create")
    @Operation(summary = "Create a new sheet", description = "Creates a new spreadsheet with the provided details")
    public ResponseEntity<ApiResponse<SheetDto>> createSheet(
            @Valid @RequestBody SheetDto sheetDto) {
        try {
            SheetDto createdSheet = sheetService.createSheet(sheetDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Sheet created successfully", createdSheet));
        } catch (Exception ex) {
            throw new RuntimeException("Failed to create sheet: " + ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get sheet by ID", description = "Retrieves a spreadsheet by its unique identifier")
    public ResponseEntity<ApiResponse<SheetDto>> getSheet(
            @Parameter(description = "Sheet ID", required = true)
            @PathVariable Long id) {
        try {
            SheetDto sheet = sheetService.getSheet(id);
            return ResponseEntity.ok(ApiResponse.success("Sheet retrieved successfully", sheet));
        } catch (Exception ex) {
            throw new RuntimeException("Failed to retrieve sheet: " + ex.getMessage());
        }
    }
}
