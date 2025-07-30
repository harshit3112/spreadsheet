package com.spreadsheet.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateSheetRequest {
    
    @NotBlank(message = "Sheet name cannot be blank")
    @Size(max = 255, message = "Sheet name cannot exceed 255 characters")
    private String name;
    
    @NotBlank(message = "User ID cannot be blank")
    private String userId;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
}
