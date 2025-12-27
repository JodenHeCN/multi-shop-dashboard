// src/main/java/com/multishop/connector/PlatformConnector.java
package com.multishop.connector;

import com.multishop.model.InventorySnapshot;
import com.multishop.model.Orders;

import java.util.List;

public interface PlatformConnector {
    /**
     * 拉取订单数据
     * @param lastSyncTime 上次同步时间
     * @return 订单列表
     */
    List<Orders> fetchOrders(java.time.LocalDateTime lastSyncTime);

    /**
     * 拉取库存数据
     * @param lastSyncTime 上次同步时间
     * @return 库存快照列表
     */
    List<InventorySnapshot> fetchInventory(java.time.LocalDateTime lastSyncTime);

    /**
     * 更新库存
     * @param sku 商品SKU
     * @param quantity 库存数量
     * @return 操作结果
     */
    boolean updateInventory(String sku, Integer quantity);

    /**
     * 获取平台标识
     * @return 平台名称
     */
    String getPlatformName();
}