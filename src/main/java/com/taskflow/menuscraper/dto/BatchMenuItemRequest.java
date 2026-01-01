package com.taskflow.menuscraper.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class BatchMenuItemRequest {

    @NotEmpty(message = "Menu items list cannot be empty")
    private List<MenuItemRequest> items;

    // Getters and Setters
    public List<MenuItemRequest> getItems() { return items; }
    public void setItems(List<MenuItemRequest> items) { this.items = items; }
}