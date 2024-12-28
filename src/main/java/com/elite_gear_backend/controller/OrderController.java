package com.elite_gear_backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elite_gear_backend.dto.OrderDTO;
import com.elite_gear_backend.dto.OrderRequestDto;
import com.elite_gear_backend.entity.User;
import com.elite_gear_backend.services.OrderService;
import com.elite_gear_backend.services.UserService;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired 
    private OrderService orderService;
    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<?> createOrderAndInitiatePayment(@RequestBody OrderRequestDto orderRequestDto) {
        User user = userService.getCurrentUser();
        String paymentUrl = orderService.createOrderAndInitiatePayment(orderRequestDto, user.getId());
        
        Map<String, String> response = new HashMap<>();
        response.put("paymentUrl", paymentUrl);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/pay/{orderId}")
    public ResponseEntity<?> payOrder(@PathVariable Long orderId) {
        User user = userService.getCurrentUser();
        String paymentUrl = orderService.payOrder(user.getId(), orderId);
        
        Map<String, String> response = new HashMap<>();
        response.put("paymentUrl", paymentUrl);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm/{orderId}")
    public ResponseEntity<?> confirmOrder(@PathVariable Long orderId) {
        User user = userService.getCurrentUser();
        orderService.confirmOrder(user.getId(), orderId);
        return ResponseEntity.ok("Order confirmed");
    }

    @GetMapping()
    public List<OrderDTO> getOrdersByUserId() {
        User user = userService.getCurrentUser();
        return orderService.getOrdersForUser(user.getId());
    }
}
