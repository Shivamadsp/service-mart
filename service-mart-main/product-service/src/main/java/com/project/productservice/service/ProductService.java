package com.project.productservice.service;

import com.project.productservice.exception.ResourceNotFoundException;
import com.project.productservice.model.ProductRequest;
import com.project.productservice.model.ProductResponse;

import java.util.List;

public interface ProductService {
    long addProduct(ProductRequest productRequest);

    List<ProductResponse> fetchAllProducts();

    ProductResponse getProductById(Long productId);

    void updateQuantity(long productId, long quantity);
}
