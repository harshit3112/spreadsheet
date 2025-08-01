package com.spreadsheet.model.dto;

import com.spreadsheet.model.enums.CellType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CellUpdate {
    
    @NotNull(message = "Row number cannot be null")
    @Min(value = 1, message = "Row number must be at least 1")
    private Integer rowNumber;
    
    @NotNull(message = "Column number cannot be null")
    @Min(value = 1, message = "Column number must be at least 1")
    private Integer columnNumber;
    
    @NotNull(message = "Cell type cannot be null")
    private CellType cellType;
    
    private Object value;
    
    private String expression;
}
