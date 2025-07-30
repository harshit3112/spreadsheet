package com.spreadsheet.repository;

import com.spreadsheet.repository.entity.SheetData;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SheetDataRepository extends JpaRepository<SheetData, Long> {
    
    @Query("SELECT sd FROM SheetData sd WHERE sd.sheet.id = :sheetId AND sd.rowNumber = :rowNumber AND sd.columnNumber = :columnNumber")
    @EntityGraph(attributePaths = {"sheet"})
    Optional<SheetData> findBySheetIdAndRowNumberAndColumnNumber(
            @Param("sheetId") Long sheetId, 
            @Param("rowNumber") Integer rowNumber, 
            @Param("columnNumber") Integer columnNumber);
    
    @Query("SELECT sd FROM SheetData sd WHERE sd.sheet.id = :sheetId")
    @EntityGraph(attributePaths = {"sheet"})
    List<SheetData> findBySheetId(@Param("sheetId") Long sheetId);
    
    @Query("SELECT sd FROM SheetData sd WHERE sd.sheet.id = :sheetId AND sd.rowNumber = :rowNumber")
    @EntityGraph(attributePaths = {"sheet"})
    List<SheetData> findBySheetIdAndRowNumber(@Param("sheetId") Long sheetId, @Param("rowNumber") Integer rowNumber);
    
    @Query("SELECT sd FROM SheetData sd WHERE sd.sheet.id = :sheetId AND sd.columnNumber = :columnNumber")
    @EntityGraph(attributePaths = {"sheet"})
    List<SheetData> findBySheetIdAndColumnNumber(@Param("sheetId") Long sheetId, @Param("columnNumber") Integer columnNumber);
}
