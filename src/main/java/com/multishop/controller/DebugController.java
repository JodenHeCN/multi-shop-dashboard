// src/main/java/com/multishop/controller/DebugController.java
package com.multishop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/debug")
public class DebugController {

    private final JdbcTemplate jdbcTemplate;

    public DebugController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/tables")
    public String showTables(Model model) {
        String sql = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' AND table_type = 'BASE TABLE';";
        List<String> tables = jdbcTemplate.queryForList(sql, String.class);
        model.addAttribute("tables", tables);
        return "debug/tables";
    }

    @GetMapping("/products")
    public String showProducts(Model model) {
        String sql = "SELECT * FROM products;";
        List<Map<String, Object>> products = jdbcTemplate.queryForList(sql);
        model.addAttribute("products", products);
        return "debug/products";
    }

    @GetMapping("/dailysales")
    public String showDailySales(Model model) {
        String sql = "SELECT * FROM daily_sales;";
        List<Map<String, Object>> dailySales = jdbcTemplate.queryForList(sql);
        model.addAttribute("dailySales", dailySales);
        return "debug/dailysales";
    }

    @GetMapping("/inventorysnapshots")
    public String showInventorySnapshots(Model model) {
        String sql = "SELECT * FROM inventory_snapshots;";
        List<Map<String, Object>> inventorySnapshots = jdbcTemplate.queryForList(sql);
        model.addAttribute("inventorySnapshots", inventorySnapshots);
        return "debug/inventorysnapshots";
    }
}