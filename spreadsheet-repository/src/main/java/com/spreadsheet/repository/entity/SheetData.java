package com.spreadsheet.repository.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "sheet_data", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"sheet_id", "row_number", "column_number"}))
@Data
public class SheetData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Row number cannot be null")
    @Min(value = 1, message = "Row number must be at least 1")
    @Column(name = "row_number", nullable = false)
    private Integer rowNumber;
    
    @NotNull(message = "Column number cannot be null")
    @Min(value = 1, message = "Column number must be at least 1")
    @Column(name = "column_number", nullable = false)
    private Integer columnNumber;
    
    @Column(name = "cell_value", columnDefinition = "TEXT")
    private String cellValue;
    
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
