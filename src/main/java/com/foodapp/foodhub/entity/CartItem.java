package com.foodapp.foodhub.entity;


import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mealName;

    @Column(name = "unit_price" ,precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    private int quantity;

    @Column(name = "sub_total", precision = 10, scale = 2)
    private BigDecimal subTotal;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "meal_id", nullable = false)
    private Meal meal;


    @PrePersist
    @PreUpdate
    private void calculateSubTotal() {
        if (unitPrice != null && quantity > 0) {
            this.subTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
}
