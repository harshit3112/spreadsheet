package com.spreadsheet.model.dto;

import com.spreadsheet.model.enums.CellType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Cell {
    
    @NotNull(message = "Cell type cannot be null")
    private CellType cellType;
    
    private Object value;
    
    private String expression;
}
