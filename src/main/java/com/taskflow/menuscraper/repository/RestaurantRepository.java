package com.taskflow.menuscraper.repository;

import com.taskflow.menuscraper.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {

    Optional<Restaurant> findBySourceUrl(String sourceUrl);

    @Query("SELECT r FROM Restaurant r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    java.util.List<Restaurant> findByNameContainingIgnoreCase(@Param("name") String name);
}