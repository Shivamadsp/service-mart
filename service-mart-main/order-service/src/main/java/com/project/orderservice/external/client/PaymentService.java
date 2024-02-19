package com.project.orderservice.external.client;

import com.project.orderservice.exception.CustomException;
import com.project.orderservice.model.PaymentRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.SneakyThrows;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "PAYMENT-SERVICE/payment")
@CircuitBreaker(name = "external", fallbackMethod = "fallback")
public interface PaymentService {
    @PostMapping
    public ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest);

    @SneakyThrows
    default ResponseEntity<Long> fallback(Exception e){
        throw new CustomException("Payment Service not available!!", "UNAVAILABLE",507);
    }
}
