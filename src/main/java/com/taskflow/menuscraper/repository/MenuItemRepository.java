package com.taskflow.menuscraper.repository;

import com.taskflow.menuscraper.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {

    @Query("SELECT mi FROM MenuItem mi " +
            "JOIN mi.restaurant r " +
            "WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :restaurantName, '%')) " +
            "OR r.sourceUrl = :sourceUrl " +
            "ORDER BY mi.scrapedAt DESC")
    List<MenuItem> findByRestaurantNameOrSourceUrl(
            @Param("restaurantName") String restaurantName,
            @Param("sourceUrl") String sourceUrl
    );

    @Query("SELECT mi FROM MenuItem mi " +
            "JOIN mi.restaurant r " +
            "WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :restaurantName, '%'))")
    List<MenuItem> findByRestaurantName(@Param("restaurantName") String restaurantName);
}