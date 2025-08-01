package com.spreadsheet.model.dto;

import com.spreadsheet.model.enums.Permission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserPermission {
    
    @NotBlank(message = "User ID cannot be blank")
    private String userId;
    
    @NotNull(message = "Permission cannot be null")
    private Permission permission;
}
