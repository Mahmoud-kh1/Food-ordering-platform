package com.foodapp.foodhub.entity;

import com.foodapp.foodhub.enums.RestaurantStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurant extends BaseEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "phone",nullable = false)
    private String phone;
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "address")
    private String address;
    @Column(name = "rating")
    private Double rating = 0.0;//should be calculated later
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private RestaurantStatus status;

    // The restaurant's  location
    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    // Restaurant's delivery ZONE
    @Column(name = "delivery_radius_km")
    private double deliveryRadiusInKm;


    @ManyToOne
    @JoinColumn(name = "subcategory_id")
    private Subcategory subcategory;
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;


    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<RestaurantSchedule> restaurantSchedule = new ArrayList<>();

}