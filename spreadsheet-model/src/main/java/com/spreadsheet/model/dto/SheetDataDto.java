package com.spreadsheet.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public record SheetDataDto(
        Long id,
        @NotNull(message = "Sheet ID cannot be null")
        Long sheetId,
        @NotNull(message = "Row number cannot be null")
        @Min(value = 1, message = "Row number must be at least 1")
        Integer rowNumber,
        @NotNull(message = "Column number cannot be null")
        @Min(value = 1, message = "Column number must be at least 1")
        Integer columnNumber,
        String cellValue
) {
    public SheetDataDto {
        if (sheetId == null) {
            throw new IllegalArgumentException("Sheet ID cannot be null");
        }
        if (rowNumber == null || rowNumber < 1) {
            throw new IllegalArgumentException("Row number must be at least 1");
        }
        if (columnNumber == null || columnNumber < 1) {
            throw new IllegalArgumentException("Column number must be at least 1");
        }
    }
}
