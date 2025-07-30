package com.spreadsheet.model.dto;

import com.spreadsheet.model.enums.Permission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserPermission(
        @NotBlank(message = "User ID cannot be blank")
        String userId,
        @NotNull(message = "Permission cannot be null")
        Permission permission
) {
    public UserPermission {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or blank");
        }
        if (permission == null) {
            throw new IllegalArgumentException("Permission cannot be null");
        }
    }
}
