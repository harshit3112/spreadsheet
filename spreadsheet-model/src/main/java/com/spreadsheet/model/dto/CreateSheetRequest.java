package com.spreadsheet.model.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateSheetRequest(
        @NotBlank(message = "User ID cannot be blank")
        String userId
) {
    public CreateSheetRequest {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or blank");
        }
    }
}
