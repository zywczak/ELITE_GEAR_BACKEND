package com.elite_gear_backend.services;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.elite_gear_backend.dto.OrderDTO;
import com.elite_gear_backend.dto.OrderRequestDto;
import com.elite_gear_backend.dto.PayURequest;
import com.elite_gear_backend.dto.PayUResponse;
import com.elite_gear_backend.entity.Basket;
import com.elite_gear_backend.entity.Order;
import com.elite_gear_backend.entity.OrderDetail;
import com.elite_gear_backend.entity.User;
import com.elite_gear_backend.exceptions.AppException;
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
        try{
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

            double totalAmount = 0.0;
            List<Basket> basketItems = basketRepository.findByUserId(userId);
            if (basketItems.isEmpty()) {
                throw new AppException("Basket is empty", HttpStatus.BAD_REQUEST);
            }
            
            for (Basket basketItem : basketItems) {
                totalAmount += basketItem.getProduct().getPrice() * basketItem.getQuantity();
            }
            Order order = new Order();
            order.setOrderDate(new Date());
            order.setUser(user);
            order.setCity(orderRequestDto.getCity());
            order.setStreet(orderRequestDto.getStreet());
            order.setHouseNumber(orderRequestDto.getHouseNumber());
            order.setPostalCode(orderRequestDto.getPostalCode());
            order.setPaid(false);
            order.setAmount(totalAmount);
            orderRepository.save(order);
            
            for (Basket basketItem : basketItems) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrder(order);
                orderDetail.setProduct(basketItem.getProduct());
                orderDetail.setQuantity(basketItem.getQuantity());
        
                orderDetailRepository.save(orderDetail);
            }
            
            basketRepository.deleteAll(basketItems);

            PayURequest payURequest = new PayURequest(totalAmount, user.getEmail(), "Order payment", order.getId());
            PayUResponse payUResponse = payUService.createPayment(payURequest);

            order.setTransactionId(payUResponse.getTransactionId());
            orderRepository.save(order);

            return payUResponse.getPaymentUrl();
        } catch (DataAccessException e) {
                throw new AppException("Error database: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (AppException e) {
                throw new AppException("Unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }  
    }

    @Transactional
    public String payOrder(Long userId, Long orderId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new AppException("Order not found", HttpStatus.NOT_FOUND));
            
        PayURequest payURequest = new PayURequest(order.getAmount(), user.getEmail(), "Order payment", orderId);
        PayUResponse payUResponse = payUService.createPayment(payURequest);

        order.setTransactionId(payUResponse.getTransactionId());
        orderRepository.save(order);
        
        return payUResponse.getPaymentUrl();
    }


    public void confirmOrder(Long userId, Long orderId) {
        try { 
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

            Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException("Unpaid order not found for user", HttpStatus.NOT_FOUND));

            if(!Objects.equals(order.getUser().getId(), user.getId())){
                throw new AppException("Not your order", HttpStatus.BAD_REQUEST);
            }

            boolean isPaymentConfirmed = payUService.verifyPayment(order.getTransactionId());

            if (!isPaymentConfirmed) {
                throw new AppException("Payment not confirmed", HttpStatus.BAD_REQUEST);
            }

            order.setPaid(true);
            orderRepository.save(order);
        } catch (DataAccessException e) {
            throw new AppException("Error database: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (AppException e) {
                throw new AppException("Unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }  
    }

    public List<OrderDTO> getOrdersForUser(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);

        if (orders.isEmpty()) {
            throw new AppException("No orders found for this user", HttpStatus.NOT_FOUND);
        }

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
