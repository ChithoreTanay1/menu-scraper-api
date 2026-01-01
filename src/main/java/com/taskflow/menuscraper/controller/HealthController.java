package com.taskflow.menuscraper.controller;

import com.taskflow.menuscraper.service.MenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Autowired
    private MenuItemService menuItemService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now().toString());

        try {
            long menuItemCount = menuItemService.getTotalMenuItems();
            long restaurantCount = menuItemService.getTotalRestaurants();

            Map<String, Object> database = new HashMap<>();
            database.put("status", "CONNECTED");
            database.put("menu_items", menuItemCount);
            database.put("restaurants", restaurantCount);

            health.put("database", database);

        } catch (Exception e) {
            Map<String, Object> database = new HashMap<>();
            database.put("status", "DISCONNECTED");
            database.put("error", e.getMessage());
            health.put("database", database);
            health.put("status", "DEGRADED");
        }

        return ResponseEntity.ok(health);
    }
}