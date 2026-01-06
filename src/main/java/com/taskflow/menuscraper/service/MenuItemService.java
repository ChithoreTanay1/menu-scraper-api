package com.taskflow.menuscraper.service;

import com.taskflow.menuscraper.dto.MenuItemRequest;
import com.taskflow.menuscraper.dto.MenuItemResponse;
import com.taskflow.menuscraper.entity.Restaurant;
import com.taskflow.menuscraper.entity.MenuItem;
import com.taskflow.menuscraper.repository.RestaurantRepository;
import com.taskflow.menuscraper.repository.MenuItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuItemService {

    private static final Logger logger = LoggerFactory.getLogger(MenuItemService.class);

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional(rollbackFor = Exception.class)
    public int saveBatch(List<MenuItemRequest> requests) {
        int savedCount = 0;

        for (MenuItemRequest request : requests) {
            try {
                // Normalize currency before validation
                if (request.getCurrency() != null) {
                    request.setCurrency(request.getCurrency().toUpperCase().trim());
                }
                validationService.validateMenuItem(request);
                saveMenuItem(request);
                savedCount++;
            } catch (Exception e) {
                logger.error("Failed to save menu item: restaurant={}, item={}, error={}",
                        request.getRestaurantName(), request.getName(), e.getMessage(), e);
                // Re-throw to trigger transaction rollback
                throw new RuntimeException("Failed to save batch: " + e.getMessage(), e);
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
        // Normalize currency to uppercase (validation ensures it's valid)
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
            Pageable pageable = PageRequest.of(0, 100);
            items = menuItemRepository.findAll(pageable).getContent();
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