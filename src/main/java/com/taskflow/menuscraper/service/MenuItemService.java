package com.taskflow.menuscraper.service;

import com.taskflow.menuscraper.dto.MenuItemRequest;
import com.taskflow.menuscraper.dto.MenuItemResponse;
import com.taskflow.menuscraper.entity.Restaurant;
import com.taskflow.menuscraper.entity.MenuItem;
import com.taskflow.menuscraper.repository.RestaurantRepository;
import com.taskflow.menuscraper.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuItemService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public int saveBatch(List<MenuItemRequest> requests) {
        int savedCount = 0;

        for (MenuItemRequest request : requests) {
            try {
                validationService.validateMenuItem(request);
                saveMenuItem(request);
                savedCount++;
            } catch (Exception e) {
                // Log error but continue with other items
                System.err.println("Failed to save menu item: " + e.getMessage());
            }
        }

        return savedCount;
    }

    @Transactional
    public void saveMenuItem(MenuItemRequest request) {
        // Find or create restaurant
        Restaurant restaurant = restaurantRepository.findBySourceUrl(request.getSourceUrl())
                .orElseGet(() -> {
                    Restaurant newRestaurant = new Restaurant(
                            request.getRestaurantName(),
                            request.getSourceUrl()
                    );
                    return restaurantRepository.save(newRestaurant);
                });

        // Update restaurant name if it has changed
        if (!restaurant.getName().equals(request.getRestaurantName())) {
            restaurant.setName(request.getRestaurantName());
            restaurantRepository.save(restaurant);
        }

        // Create menu item
        MenuItem menuItem = new MenuItem();
        menuItem.setRestaurant(restaurant);
        menuItem.setName(request.getName());
        menuItem.setDescription(request.getDescription());
        menuItem.setPrice(request.getPrice());
        menuItem.setCurrency(request.getCurrency().toUpperCase());

        menuItemRepository.save(menuItem);
    }

    public List<MenuItemResponse> getMenuItems(String restaurantName, String sourceUrl) {
        List<MenuItem> items;

        if (restaurantName != null && !restaurantName.trim().isEmpty()) {
            if (sourceUrl != null && !sourceUrl.trim().isEmpty()) {
                items = menuItemRepository.findByRestaurantNameOrSourceUrl(restaurantName, sourceUrl);
            } else {
                items = menuItemRepository.findByRestaurantName(restaurantName);
            }
        } else if (sourceUrl != null && !sourceUrl.trim().isEmpty()) {
            items = menuItemRepository.findByRestaurantNameOrSourceUrl("", sourceUrl);
        } else {
            // Return all items if no filter provided (limit to 100 for safety)
            items = menuItemRepository.findAll().stream()
                    .limit(100)
                    .collect(Collectors.toList());
        }

        return items.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private MenuItemResponse convertToResponse(MenuItem item) {
        MenuItemResponse response = new MenuItemResponse();
        response.setId(item.getId());
        response.setRestaurantName(item.getRestaurant().getName());
        response.setSourceUrl(item.getRestaurant().getSourceUrl());
        response.setName(item.getName());
        response.setDescription(item.getDescription());
        response.setPrice(item.getPrice());
        response.setCurrency(item.getCurrency());
        response.setScrapedAt(item.getScrapedAt());
        return response;
    }

    public long getTotalMenuItems() {
        return menuItemRepository.count();
    }

    public long getTotalRestaurants() {
        return restaurantRepository.count();
    }
}