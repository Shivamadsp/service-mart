package com.project.orderservice.service;

import com.project.orderservice.model.OrderRequest;
import com.project.orderservice.model.OrderResponse;

public interface OrderService {
    long placeOrder(OrderRequest orderRequest);

    OrderResponse getOrderDetails(long orderId);
}
