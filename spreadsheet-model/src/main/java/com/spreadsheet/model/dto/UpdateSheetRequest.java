package com.spreadsheet.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record UpdateSheetRequest(
        @NotEmpty(message = "Cells list cannot be empty")
        @Valid
        List<CellUpdate> cells
) {
    public UpdateSheetRequest {
        if (cells == null || cells.isEmpty()) {
            throw new IllegalArgumentException("Cells list cannot be null or empty");
        }
    }
}
