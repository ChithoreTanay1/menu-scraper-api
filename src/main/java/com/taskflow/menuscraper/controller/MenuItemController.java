package com.taskflow.menuscraper.controller;

import com.taskflow.menuscraper.dto.BatchMenuItemRequest;
import com.taskflow.menuscraper.dto.MenuItemResponse;
import com.taskflow.menuscraper.service.MenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/menu-items")
public class MenuItemController {

    @Autowired
    private MenuItemService menuItemService;

    @PostMapping("/batch")
    public ResponseEntity<?> saveBatch(@Valid @RequestBody BatchMenuItemRequest request) {
        try {
            int savedCount = menuItemService.saveBatch(request.getItems());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Batch processed successfully");
            response.put("saved_count", savedCount);
            response.put("total_requested", request.getItems().size());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Validation failed");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            error.put("message", "Failed to process batch request");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping
    public ResponseEntity<List<MenuItemResponse>> getMenuItems(
            @RequestParam(required = false) String restaurant,
            @RequestParam(value = "source_url", required = false) String sourceUrl) {

        List<MenuItemResponse> items = menuItemService.getMenuItems(restaurant, sourceUrl);
        return ResponseEntity.ok(items);
    }
}