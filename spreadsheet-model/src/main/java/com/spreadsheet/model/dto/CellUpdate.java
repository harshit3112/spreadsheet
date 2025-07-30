package com.spreadsheet.model.dto;

import com.spreadsheet.model.enums.CellType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CellUpdate(
        @NotNull(message = "Row number cannot be null")
        @Min(value = 1, message = "Row number must be at least 1")
        Integer rowNumber,
        @NotNull(message = "Column number cannot be null")
        @Min(value = 1, message = "Column number must be at least 1")
        Integer columnNumber,
        @NotNull(message = "Cell type cannot be null")
        CellType cellType,
        Object value,
        String expression
) {
    public CellUpdate {
        if (rowNumber == null || rowNumber < 1) {
            throw new IllegalArgumentException("Row number must be at least 1");
        }
        if (columnNumber == null || columnNumber < 1) {
            throw new IllegalArgumentException("Column number must be at least 1");
        }
        if (cellType == null) {
            throw new IllegalArgumentException("Cell type cannot be null");
        }
        if (cellType == CellType.EXPRESSION && (expression == null || expression.isBlank())) {
            throw new IllegalArgumentException("Expression cannot be null or blank for EXPRESSION cell type");
        }
    }
}
