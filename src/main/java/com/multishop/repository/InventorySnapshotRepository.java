// src/main/java/com/multishop/repository/InventorySnapshotRepository.java
package com.multishop.repository;

import com.multishop.model.InventorySnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventorySnapshotRepository extends JpaRepository<InventorySnapshot, Long> {
    List<InventorySnapshot> findByProductIdAndSnapshotTimeBetween(Long productId, LocalDateTime startTime, LocalDateTime endTime);
    
    List<InventorySnapshot> findByStoreId(Long storeId);
    
    List<InventorySnapshot> findBySku(String sku);
}