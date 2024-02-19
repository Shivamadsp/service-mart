package com.project.orderservice.external.client;

import com.project.orderservice.exception.CustomException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.SneakyThrows;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "PRODUCT-SERVICE/product")
@CircuitBreaker(name = "external",fallbackMethod = "fallback")
public interface ProductService {
    @PutMapping("/updateQuantity/{id}")
    ResponseEntity<Void> updateProductQuantity(@PathVariable("id") long productId, @RequestParam("quantity") long quantity);
    @SneakyThrows
    default ResponseEntity<Void> fallback(Exception e){
        throw new CustomException("Product Service not available!!", "UNAVAILABLE",507);
    }
}
