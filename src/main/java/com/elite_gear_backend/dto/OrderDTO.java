package com.elite_gear_backend.dto;

import java.util.Date;
import java.util.List;

public class OrderDTO {
    private Long orderId;
    private Date orderDate;
    private double amount;
    private boolean paid;
    private List<ProductOrderDTO> products;

    public OrderDTO(Long orderId, Date orderDate, double amount, boolean paid, List<ProductOrderDTO> products) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.amount = amount;
        this.paid = paid;
        this.products = products;
    }

    public static class ProductOrderDTO {  // Make it static
        private Long productId;
        private String manufacturer;
        private String model;
        private double price;
        private int quantity;

        public ProductOrderDTO(Long productId, String manufacturer, String model, double price, int quantity) {
            this.productId = productId;
            this.manufacturer = manufacturer;
            this.model = model;
            this.price = price;
            this.quantity = quantity;
        }

            // Getters and setters
            public Long getProductId() {
                return productId;
            }

            public void setProductId(Long productId) {
                this.productId = productId;
            }

            public String getManufacturer() {
                return manufacturer;
            }

            public void setManufacturer(String manufacturer) {
                this.manufacturer = manufacturer;
            }

            public String getModel() {
                return model;
            }

            public void setModel(String model) {
                this.model = model;
            }

            public double getPrice() {
                return price;
            }

            public void setPrice(double price) {
                this.price = price;
            }

            public int getQuantity() {
                return quantity;
            }

            public void setQuantity(int quantity) {
                this.quantity = quantity;
            }
        }
   // Getters and Setters
   public Long getOrderId() {
    return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public List<ProductOrderDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductOrderDTO> products) {
        this.products = products;
    }
}
