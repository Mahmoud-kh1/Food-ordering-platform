package com.foodapp.foodhub.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String Address;
    private String Phone;
    private String Notes;
    private String area;
    private String street;
    private String building;
    private String floor;
    private String apartment;


    @OneToOne(mappedBy = "orderAddress")
    private Order order;
}
