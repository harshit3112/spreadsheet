package com.spreadsheet.repository.entity;

import com.spreadsheet.model.enums.Permission;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "sheet_permission", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"sheet_id", "user_id"}))
@Data
public class SheetPermission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "User ID cannot be blank")
    @Column(name = "user_id", nullable = false, length = 255)
    private String userId;
    
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Permission cannot be null")
    @Column(name = "permission", nullable = false)
    private Permission permission;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sheet_id", nullable = false)
    private Sheet sheet;
}
