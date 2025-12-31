package com.taskflow.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "restaurants",
        uniqueConstraints = @UniqueConstraint(columnNames = "source_url"))
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Restaurant name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Source URL is required")
    @Column(name = "source_url", nullable = false, unique = true)
    private String sourceUrl;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MenuItem> menuItems = new HashSet<>();

    public Restaurant() {}

    public Restaurant(String name, String sourceUrl) {
        this.name = name;
        this.sourceUrl = sourceUrl;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }

    public Set<MenuItem> getMenuItems() { return menuItems; }
    public void setMenuItems(Set<MenuItem> menuItems) { this.menuItems = menuItems; }
}