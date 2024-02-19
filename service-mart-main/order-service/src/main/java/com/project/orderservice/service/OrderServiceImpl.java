package com.project.orderservice.service;

import com.project.orderservice.entity.Order;
import com.project.orderservice.exception.CustomException;
import com.project.orderservice.external.client.PaymentService;
import com.project.orderservice.external.client.ProductService;
import com.project.orderservice.external.response.PaymentResponse;
import com.project.orderservice.external.response.ProductResponse;
import com.project.orderservice.model.OrderRequest;
import com.project.orderservice.model.OrderResponse;
import com.project.orderservice.model.PaymentRequest;
import com.project.orderservice.repository.OrderRepository;
import lombok.CustomLog;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService{
    @Autowired
    private  OrderRepository orderRepository;
    @Autowired
    private  ProductService productService;
    @Autowired
    private  PaymentService paymentService;
    @Autowired
    private  RestTemplate restTemplate;
    @Override
    public long placeOrder(OrderRequest orderRequest) {
        //Order Entity - save the order status
        //Product Service - block products and reduce quantity
        //Payment Service - success -> complete else cancelled
        log.info("Placing Order Request {}",orderRequest);
        productService.updateProductQuantity(orderRequest.getProductId(),orderRequest.getQuantity());
        log.info("CREATING ORDER..");
        Order order = Order.builder()
                .productId(orderRequest.getProductId())
                .amount(orderRequest.getTotalAmount())
                .quantity(orderRequest.getQuantity())
                .orderDate(Instant.now())
                .orderStatus("CREATED")
                .build();
        order = orderRepository.save(order);
        log.info("Calling Payment Service to complete the payment...");
        String orderStatus = null;
        try{
            paymentService.doPayment(PaymentRequest.builder()
                    .orderId(order.getOrderId())
                    .amount(order.getAmount())
                    .paymentMode(orderRequest.getPaymentMode())
                    .build());
            log.info("Payment successful, changing order status to placed!");
            orderStatus = "PLACED";
        }
        catch(Exception e){
            log.info("Payment failed!!");
            orderStatus = "PAYMENT_FAILED";
        }
        order.setOrderStatus(orderStatus);
        orderRepository.save(order);
        log.info("Order placed successfully with order id {}",order.getOrderId());
        return order.getOrderId();
    }

    @Override
    @SneakyThrows
    public OrderResponse getOrderDetails(long orderId) {
        log.info("Getting order details for Id : {}",orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException("Order not found with id "+orderId,"NOT_FOUND",404));
       log.info("Invoking product service to fetch the product details..");
        ProductResponse productResponse = restTemplate.getForObject("http://PRODUCT-SERVICE/product/"+order.getProductId(), ProductResponse.class);

        log.info("Getting payment details from payment service...");
        PaymentResponse paymentResponse = restTemplate.getForObject("http://PAYMENT-SERVICE/payment/order/"+order.getOrderId(),
                PaymentResponse.class);

        OrderResponse.ProductDetails productDetails = OrderResponse.ProductDetails.builder()
                .productId(productResponse.getProductId())
                .productName(productResponse.getProductName())
                .price(productResponse.getPrice())
                .quantity(productResponse.getQuantity())
                .build();
        OrderResponse.PaymentDetails paymentDetails = OrderResponse.PaymentDetails.builder()
                .paymentId(paymentResponse.getPaymentId())
                .orderId(paymentResponse.getOrderId())
                .paymentDate(paymentResponse.getPaymentDate())
                .amount(paymentResponse.getAmount())
                .paymentMode(paymentResponse.getPaymentMode())
                .status(paymentResponse.getStatus())
                .build();
        OrderResponse orderResponse = OrderResponse.builder()
                .orderId(order.getOrderId())
                .orderStatus(order.getOrderStatus())
                .amount(order.getAmount())
                .orderDate(order.getOrderDate())
                .productDetails(productDetails)
                .paymentDetails(paymentDetails)
                .build();
        return orderResponse;
    }
}
