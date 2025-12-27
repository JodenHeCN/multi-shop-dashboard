// src/main/java/com/multishop/repository/OrdersRepository.java
package com.multishop.repository;

import com.multishop.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {
    List<Orders> findByStoreIdAndCreatedAtAfter(Long storeId, LocalDateTime dateTime);
    
    List<Orders> findByProductId(Long productId);
    
    List<Orders> findBySku(String sku);
}