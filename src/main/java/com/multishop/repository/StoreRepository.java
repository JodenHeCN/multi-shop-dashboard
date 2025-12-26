// src/main/java/com/multishop/repository/StoreRepository.java
package com.multishop.repository;

import com.multishop.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findByEnabledTrue();
    
    List<Store> findByPlatform(Store.Platform platform);
    
    Store findByPlatformAndPlatformStoreId(Store.Platform platform, String platformStoreId);
}