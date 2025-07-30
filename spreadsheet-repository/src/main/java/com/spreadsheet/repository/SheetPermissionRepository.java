package com.spreadsheet.repository;

import com.spreadsheet.repository.entity.SheetPermission;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SheetPermissionRepository extends JpaRepository<SheetPermission, Long> {
    
    @Query("SELECT sp FROM SheetPermission sp WHERE sp.sheet.id = :sheetId")
    @EntityGraph(attributePaths = {"sheet"})
    List<SheetPermission> findBySheetId(@Param("sheetId") Long sheetId);
    
    @Query("SELECT sp FROM SheetPermission sp WHERE sp.sheet.id = :sheetId AND sp.userId = :userId")
    @EntityGraph(attributePaths = {"sheet"})
    Optional<SheetPermission> findBySheetIdAndUserId(@Param("sheetId") Long sheetId, @Param("userId") String userId);
    
    @Query("SELECT sp FROM SheetPermission sp WHERE sp.userId = :userId")
    @EntityGraph(attributePaths = {"sheet"})
    List<SheetPermission> findByUserId(@Param("userId") String userId);
}
