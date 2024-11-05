package com.elite_gear_backend.services;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elite_gear_backend.dto.OrderDTO;
import com.elite_gear_backend.dto.OrderRequestDto;
import com.elite_gear_backend.dto.PayURequest;
import com.elite_gear_backend.dto.PayUResponse;
import com.elite_gear_backend.entity.Basket;
import com.elite_gear_backend.entity.Order;
import com.elite_gear_backend.entity.OrderDetail;
import com.elite_gear_backend.entity.User;
import com.elite_gear_backend.repository.BasketRepository;
import com.elite_gear_backend.repository.OrderDetailRepository;
import com.elite_gear_backend.repository.OrderRepository;
import com.elite_gear_backend.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PayUService payUService;

    @Transactional
    public String createOrderAndInitiatePayment(OrderRequestDto orderRequestDto, Long userId) {
        // Fetch user, basket items, and calculate total amount as before
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Initialize total amount for the order
        double totalAmount = 0.0;
        List<Basket> basketItems = basketRepository.findByUserId(userId);
        // Calculate the total amount from the basket items
        for (Basket basketItem : basketItems) {
            totalAmount += basketItem.getProduct().getPrice() * basketItem.getQuantity();
        }
        System.out.println(totalAmount);
        // Create and initialize the Order object before saving
        Order order = new Order();
        order.setOrderDate(new Date());
        order.setUser(user);
        order.setCity(orderRequestDto.getCity());
        order.setStreet(orderRequestDto.getStreet());
        order.setHouseNumber(orderRequestDto.getHouseNumber());
        order.setPostalCode(orderRequestDto.getPostalCode());
        order.setPaid(false);
        order.setAmount(totalAmount);  // Set the total amount before saving the order
        // Save th  e order with the total amount set
        order = orderRepository.save(order);
        // Create and save OrderDetail entities for each item in the basket
        for (Basket basketItem : basketItems) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order); // Set reference to the saved Order
            orderDetail.setProduct(basketItem.getProduct());
            orderDetail.setQuantity(basketItem.getQuantity());
    
            // Save OrderDetail to repository
            orderDetailRepository.save(orderDetail);
        }
        // // Clear the user's basket after order creation
        basketRepository.deleteAll(basketItems);

        // Create a payment request to PayU
        PayURequest payURequest = new PayURequest(totalAmount, user.getEmail(), "Order payment");
        PayUResponse payUResponse = payUService.createPayment(payURequest);

        // Return the PayU payment URL for frontend to redirect the user
        return payUResponse.getPaymentUrl();
    }


    public void confirmOrder(Long userId, String payUTransactionId) {
        // Verify payment with PayU using the transaction ID
        boolean isPaymentConfirmed = payUService.verifyPayment(payUTransactionId);

        if (!isPaymentConfirmed) {
            throw new IllegalStateException("Payment not confirmed");
        }

        // Continue with order creation and save it to the database as in the original code
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Set order details here and save to repository
        // Find the latest order for the user that hasn't been marked as paid yet
        Order order = orderRepository.findTopByUserIdAndPaidFalseOrderByOrderDateDesc(userId)
            .orElseThrow(() -> new IllegalStateException("Unpaid order not found for user"));

        // Mark the order as paid
        order.setPaid(true);
        orderRepository.save(order);
    }

    public List<OrderDTO> getOrdersForUser(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);

        return orders.stream().map(order -> {
            List<OrderDTO.ProductOrderDTO> products = orderDetailRepository.findByOrderId(order.getId())
                .stream()
                .map(detail -> new OrderDTO.ProductOrderDTO(
                        detail.getProduct().getId(),
                        detail.getProduct().getManufacturer(),
                        detail.getProduct().getModel(),
                        detail.getProduct().getPrice(),
                        detail.getQuantity()
                ))
                .collect(Collectors.toList());

            return new OrderDTO(
                    order.getId(),
                    order.getOrderDate(),
                    order.getAmount(),
                    order.isPaid(),
                    products
            );
        }).collect(Collectors.toList());
    }
}

