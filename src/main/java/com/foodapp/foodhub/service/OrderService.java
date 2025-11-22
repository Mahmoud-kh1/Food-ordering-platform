package com.foodapp.foodhub.service;

import com.foodapp.foodhub.dto.CheckoutRequestDTO;
import com.foodapp.foodhub.entity.*;
import com.foodapp.foodhub.enums.OrderStatus;
import com.foodapp.foodhub.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository; // if needed
    private final UserRepository userRepository;
    private final ZoneService zoneService;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order checkout(CheckoutRequestDTO request) {


        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));

        // CHECK the customer's delivery address is still in the zone
        boolean isInZone = zoneService.isWithinDeliveryZone(
                request.getUserLatitude(),
                request.getUserLongitude(),
                restaurant.getLatitude(),
                restaurant.getLongitude(),
                restaurant.getDeliveryRadiusInKm()
        );

        if (!isInZone) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Delivery address is outside the restaurant's zone.");
        }
        Cart cart = cartRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        User user = cart.getUser();

        // Create Order Address
        OrderAddress address = new OrderAddress();
        address.setAddress(request.getAddress());
        address.setPhone(request.getPhone());
        address.setNotes(request.getNotes());
        address.setArea(request.getArea());
        address.setStreet(request.getStreet());
        address.setBuilding(request.getBuilding());
        address.setFloor(request.getFloor());
        address.setApartment(request.getApartment());

        // 3 Create Order entity
        Order order = new Order();
        order.setUser(user);
        order.setRestaurant(cart.getRestaurant());
        order.setOrderAddress(address);
        order.setStatus(OrderStatus.PENDING);

        Set<OrderItem> orderItems = new HashSet<>();
        BigDecimal total = BigDecimal.ZERO;

        // Copy CartItems to OrderItems
        for (CartItem cartItem : cart.getItems()) {

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getMeal().getPrice());

            orderItems.add(orderItem);

            // Calculate total
            total = total.add(
                    cartItem.getMeal().getPrice()
                            .multiply(BigDecimal.valueOf(cartItem.getQuantity()))
            );
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(total);


        Order savedOrder = orderRepository.save(order);

        //  Delete cart after checkout
        cartItemRepository.deleteAll(cart.getItems());
        cartRepository.delete(cart);

        return savedOrder;
    }

    public Order updateStatus(Long orderId, OrderStatus newStatus) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatus currentStatus = order.getStatus();

        if (!isValidTransition(currentStatus, newStatus)) {
            throw new RuntimeException("Invalid status transition: "
                    + currentStatus + " → " + newStatus);
        }

        order.setStatus(newStatus);

        return orderRepository.save(order);
    }




    private boolean isValidTransition(OrderStatus current, OrderStatus next) {

        return switch (current) {
            case PENDING -> (next == OrderStatus.CONFIRMED || next == OrderStatus.CANCELLED);

            case CONFIRMED -> (next == OrderStatus.PREPARING || next == OrderStatus.CANCELLED);

            case PREPARING -> (next == OrderStatus.OUT_FOR_DELIVERY);

            case OUT_FOR_DELIVERY -> (next == OrderStatus.DELIVERED);

            case DELIVERED -> false; // Delivered is final.

            case CANCELLED -> false; // Cancelled is final.
        };
    }


}
