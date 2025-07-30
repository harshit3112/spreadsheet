package com.spreadsheet.model.dto;

import com.spreadsheet.model.enums.CellType;
import jakarta.validation.constraints.NotNull;

public record Cell(
        @NotNull(message = "Cell type cannot be null")
        CellType cellType,
        Object value,
        String expression
) {
    public Cell {
        if (cellType == null) {
            throw new IllegalArgumentException("Cell type cannot be null");
        }
        if (cellType == CellType.EXPRESSION && (expression == null || expression.isBlank())) {
            throw new IllegalArgumentException("Expression cannot be null or blank for EXPRESSION cell type");
        }
    }
}
