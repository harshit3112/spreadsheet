package com.spreadsheet.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

public record SheetResponse(
        @NotNull(message = "Created at cannot be null")
        Long createdAt,
        @NotBlank(message = "User ID cannot be blank")
        String userId,
        Map<String, Cell> sheetData,
        List<UserPermission> userPermissions
) {
    public SheetResponse {
        if (createdAt == null) {
            throw new IllegalArgumentException("Created at cannot be null");
        }
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or blank");
        }
    }
}
