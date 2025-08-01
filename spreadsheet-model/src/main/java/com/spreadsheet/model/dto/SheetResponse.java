package com.spreadsheet.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SheetResponse {
    
    @NotNull(message = "Created at cannot be null")
    private Long createdAt;
    
    @NotBlank(message = "User ID cannot be blank")
    private String userId;
    
    private Map<String, Cell> sheetData;
    
    private List<UserPermission> userPermissions;
}
