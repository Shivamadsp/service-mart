package com.project.orderservice.service;

import com.project.orderservice.entity.Order;
import com.project.orderservice.exception.CustomException;
import com.project.orderservice.external.client.PaymentService;
import com.project.orderservice.external.client.ProductService;
import com.project.orderservice.external.response.PaymentResponse;
import com.project.orderservice.external.response.ProductResponse;
import com.project.orderservice.model.OrderRequest;
import com.project.orderservice.model.OrderResponse;
import com.project.orderservice.model.PaymentMode;
import com.project.orderservice.model.PaymentRequest;
import com.project.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.time.Instant;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@SpringBootTest
class OrderServiceImplTest {
    @Mock
    private  OrderRepository orderRepository;
    @Mock
    private  ProductService productService;
    @Mock
    private  PaymentService paymentService;
    @Mock
    private  RestTemplate restTemplate;
    @InjectMocks
    OrderService orderService = new OrderServiceImpl();
    @DisplayName("Get Order : Success Scenario") //when reports are generated it will have this name
    @Test
    void test_When_Order_Success(){
        //Mocking
        Order order = getMockOrder();
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(restTemplate.getForObject("http://PRODUCT-SERVICE/product/"+order.getProductId(),
                ProductResponse.class)).thenReturn(getMockProductResponse());
        when(restTemplate.getForObject("http://PAYMENT-SERVICE/payment/order/"+order.getOrderId(),
                PaymentResponse.class)).thenReturn(getMockPaymentResponse());
        //Actual method call
        OrderResponse orderResponse = orderService.getOrderDetails(1);

        //verification
        verify(orderRepository, times(1)).findById(anyLong()); // verifies that findById was called only 1 time in the method , it will also let us know that this call was made or not
        verify(restTemplate, times(1)).getForObject("http://PRODUCT-SERVICE/product/"+order.getProductId(),
                ProductResponse.class);
        verify(restTemplate, times(1)).getForObject("http://PAYMENT-SERVICE/payment/order/"+order.getOrderId(),
                PaymentResponse.class);

        //Assertions
        assertNotNull(orderResponse);
        assertEquals(order.getOrderId(), orderResponse.getOrderId());
    }
    @DisplayName("Get Orders : Failure Scenario")
    @Test
    void test_When_Get_Order_Not_Found(){
        when(orderRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));
        CustomException exception = assertThrows(CustomException.class, ()->orderService.getOrderDetails(1));
        assertEquals("NOT_FOUND", exception.getErrorCode());
        assertEquals(404, exception.getStatus());
        verify(orderRepository, times(1)).findById(anyLong());
    }
    @DisplayName("Place Order : Success Scenario")
    @Test
    void test_When_Place_Order_Success() {
        Order order = getMockOrder();
        OrderRequest orderRequest = getMockOrderRequest();

        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(productService.updateProductQuantity(anyLong(),anyLong())).thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
        when(paymentService.doPayment(any(PaymentRequest.class))).thenReturn(new ResponseEntity<Long>(1L,HttpStatus.OK));

        long orderId = orderService.placeOrder(orderRequest);

        verify(orderRepository, times(2)).save(any());
        verify(productService, times(1)).updateProductQuantity(anyLong(),anyLong());
        verify(paymentService, times(1)).doPayment(any(PaymentRequest.class));

        assertEquals(order.getOrderId(), orderId);
    }
    @DisplayName("Place Order: Payment Failure Scenario")
    @Test
    void test_When_Place_Order_Payment_Fails_Order_Placed(){
        Order order = getMockOrder();
        OrderRequest orderRequest = getMockOrderRequest();

        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(productService.updateProductQuantity(anyLong(),anyLong())).thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
        when(paymentService.doPayment(any(PaymentRequest.class))).thenThrow(new RuntimeException());

        long orderId = orderService.placeOrder(orderRequest);
        verify(orderRepository, times(2)).save(any());
        verify(productService, times(1)).updateProductQuantity(anyLong(),anyLong());
        verify(paymentService, times(1)).doPayment(any(PaymentRequest.class));

        assertEquals(order.getOrderId(), orderId);
    }
    private OrderRequest getMockOrderRequest() {
        return OrderRequest.builder()
                .productId(2)
                .paymentMode(PaymentMode.PAYPAL)
                .quantity(5)
                .totalAmount(10000)
                .build();
    }

    private PaymentResponse getMockPaymentResponse() {
        return PaymentResponse.builder()
                .paymentId(1)
                .orderId(1)
                .paymentDate(Instant.now())
                .amount(10000)
                .paymentMode(PaymentMode.PAYPAL)
                .status("ACCEPTED").build();
    }

    private ProductResponse getMockProductResponse() {
        return ProductResponse.builder()
                .price(10000)
                .productId(2)
                .productName("iPhone")
                .quantity(10).build();
    }

    private Order getMockOrder() {
        return Order.builder()
                .orderId(1)
                .orderStatus("PLACED")
                .orderDate(Instant.now())
                .amount(10000)
                .quantity(5)
                .productId(2)
                .build();
    }
}