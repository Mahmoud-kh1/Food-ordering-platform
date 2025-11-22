package com.foodapp.foodhub.controller;

import com.foodapp.foodhub.dto.CheckoutRequestDTO;
import com.foodapp.foodhub.entity.Order;
import com.foodapp.foodhub.enums.OrderStatus;
import com.foodapp.foodhub.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/allorders")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/checkout")
    public ResponseEntity<Order> checkout(@RequestBody CheckoutRequestDTO request) {
        // send notification here for user and restaurant_admin
        Order order = orderService.checkout(request);
        return ResponseEntity.ok(order);
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Order> updateStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status
    ) {
        // send notification here for user and restaurant_admin
        Order order = orderService.updateStatus(orderId, status);
        return ResponseEntity.ok(order);
    }


}
