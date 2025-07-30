package com.spreadsheet.repository;

import com.spreadsheet.repository.entity.Sheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SheetRepository extends JpaRepository<Sheet, Long> {
    
    @Query("SELECT s FROM Sheet s WHERE s.name = :name")
    Optional<Sheet> findByName(@Param("name") String name);
    
    @Query("SELECT s FROM Sheet s WHERE s.id = :id")
    Optional<Sheet> findById(@Param("id") Long id);
    
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Sheet s WHERE s.name = :name")
    boolean existsByName(@Param("name") String name);
}
