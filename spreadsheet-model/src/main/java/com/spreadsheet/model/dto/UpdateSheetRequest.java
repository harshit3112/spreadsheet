package com.spreadsheet.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class UpdateSheetRequest {
    
    @NotEmpty(message = "Cells list cannot be empty")
    @Valid
    private List<CellUpdate> cells;
}
