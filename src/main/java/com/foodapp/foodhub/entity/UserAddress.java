package com.foodapp.foodhub.entity;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String street;
    private String building;
    private String floor;
    private String apartment;
    private String area;
    private Double latitude;
    private Double longitude;
    private String phone;
    private String notes;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

//    @ManyToOne
//    @JoinColumn(name = "zone_id", nullable = false)
//    private Zone zone;
}

