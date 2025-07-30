package com.spreadsheet.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SheetDto(
        Long id,
        @NotBlank(message = "Sheet name cannot be blank")
        @Size(max = 255, message = "Sheet name cannot exceed 255 characters")
        String name,
        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description,
        Integer rowCount,
        Integer columnCount
) {
    public SheetDto {
        if (name != null && name.isBlank()) {
            throw new IllegalArgumentException("Sheet name cannot be blank");
        }
        if (name != null && name.length() > 255) {
            throw new IllegalArgumentException("Sheet name cannot exceed 255 characters");
        }
        if (description != null && description.length() > 500) {
            throw new IllegalArgumentException("Description cannot exceed 500 characters");
        }
    }
}
