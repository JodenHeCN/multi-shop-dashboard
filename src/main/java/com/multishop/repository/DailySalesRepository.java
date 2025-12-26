// src/main/java/com/multishop/repository/DailySalesRepository.java
package com.multishop.repository;

import com.multishop.model.DailySales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailySalesRepository extends JpaRepository<DailySales, Long> {
    List<DailySales> findByProductIdAndDateBetween(Long productId, LocalDate startDate, LocalDate endDate);
    
    List<DailySales> findByDate(LocalDate date);
    
    List<DailySales> findByStoreIdAndDate(Long storeId, LocalDate date);
}