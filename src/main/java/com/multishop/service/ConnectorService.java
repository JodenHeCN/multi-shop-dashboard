// src/main/java/com/multishop/service/ConnectorService.java
package com.multishop.service;

import com.multishop.connector.PlatformConnector;
import com.multishop.model.InventorySnapshot;
import com.multishop.model.Orders;
import com.multishop.model.Store;
import com.multishop.repository.InventorySnapshotRepository;
import com.multishop.repository.OrdersRepository;
import com.multishop.repository.StoreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConnectorService {

    private static final Logger logger = LoggerFactory.getLogger(ConnectorService.class);

    private final Map<String, PlatformConnector> connectors = new ConcurrentHashMap<>();

    private final OrdersRepository ordersRepository;
    private final InventorySnapshotRepository inventorySnapshotRepository;
    private final StoreRepository storeRepository;

    public ConnectorService(
            OrdersRepository ordersRepository,
            InventorySnapshotRepository inventorySnapshotRepository,
            StoreRepository storeRepository) {
        this.ordersRepository = ordersRepository;
        this.inventorySnapshotRepository = inventorySnapshotRepository;
        this.storeRepository = storeRepository;
    }

    public void setConnectors(List<PlatformConnector> platformConnectors) {
        for (PlatformConnector connector : platformConnectors) {
            connectors.put(connector.getPlatformName(), connector);
        }
    }

    /**
     * 定时任务：同步订单和库存数据
     */
    @Scheduled(fixedRate = 300000) // 每5分钟执行一次
    public void syncData() {
        logger.info("Starting data sync from all platforms");

        for (Store store : storeRepository.findByEnabledTrue()) {
            syncOrdersForStore(store);
            syncInventoryForStore(store);
        }

        logger.info("Data sync completed");
    }

    /**
     * 同步指定店铺的订单数据
     */
    public void syncOrdersForStore(Store store) {
        PlatformConnector connector = connectors.get(store.getPlatform().name());
        if (connector == null) {
            logger.warn("No connector found for platform: {}", store.getPlatform());
            return;
        }

        try {
            // 获取上次同步时间，如果没有则默认为7天前
            LocalDateTime lastSync = getLastSyncTime(store.getId(), "orders");
            List<Orders> orders = connector.fetchOrders(lastSync);

            // 保存订单数据
            for (Orders order : orders) {
                order.setStoreId(store.getId());
                ordersRepository.save(order);
            }

            logger.info("Synced {} orders from {} store: {}", orders.size(), store.getPlatform(), store.getName());
        } catch (Exception e) {
            logger.error("Error syncing orders for store: {}", store.getName(), e);
        }
    }

    /**
     * 同步指定店铺的库存数据
     */
    public void syncInventoryForStore(Store store) {
        PlatformConnector connector = connectors.get(store.getPlatform().name());
        if (connector == null) {
            logger.warn("No connector found for platform: {}", store.getPlatform());
            return;
        }

        try {
            // 获取上次同步时间，如果没有则默认为1小时前
            LocalDateTime lastSync = getLastSyncTime(store.getId(), "inventory");
            List<InventorySnapshot> snapshots = connector.fetchInventory(lastSync);

            // 保存库存快照数据
            for (InventorySnapshot snapshot : snapshots) {
                snapshot.setStoreId(store.getId().intValue());
                inventorySnapshotRepository.save(snapshot);
            }

            logger.info("Synced {} inventory snapshots from {} store: {}", snapshots.size(), store.getPlatform(), store.getName());
        } catch (Exception e) {
            logger.error("Error syncing inventory for store: {}", store.getName(), e);
        }
    }

    /**
     * 更新指定平台的库存
     */
    public boolean updateInventory(Long storeId, String sku, Integer quantity) {
        Store store = storeRepository.findById(storeId).orElse(null);
        if (store == null) {
            logger.warn("Store not found: {}", storeId);
            return false;
        }

        PlatformConnector connector = connectors.get(store.getPlatform().name());
        if (connector == null) {
            logger.warn("No connector found for platform: {}", store.getPlatform());
            return false;
        }

        try {
            return connector.updateInventory(sku, quantity);
        } catch (Exception e) {
            logger.error("Error updating inventory for store: {}, sku: {}", storeId, sku, e);
            return false;
        }
    }

    /**
     * 获取上次同步时间（模拟实现，实际中可能需要存储在数据库中）
     */
    private LocalDateTime getLastSyncTime(Long storeId, String dataType) {
        // 这里是模拟实现，实际中应该从数据库中获取上次同步时间
        if ("orders".equals(dataType)) {
            return LocalDateTime.now().minusDays(7); // 默认7天前
        } else {
            return LocalDateTime.now().minusHours(1); // 默认1小时前
        }
    }
}