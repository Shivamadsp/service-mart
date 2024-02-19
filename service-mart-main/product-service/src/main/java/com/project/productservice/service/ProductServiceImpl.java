package com.project.productservice.service;

import com.project.productservice.entity.Product;
import com.project.productservice.exception.ProductNotAvailableException;
import com.project.productservice.exception.ResourceNotFoundException;
import com.project.productservice.model.ProductRequest;
import com.project.productservice.model.ProductResponse;
import com.project.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService{
    private final ProductRepository productRepository;

    @Override
    public long addProduct(ProductRequest productRequest) {
        log.info("Adding product...");
        Product product = Product.builder()
                .productName(productRequest.getName())
                .price(productRequest.getPrice())
                .quantity(productRequest.getQuantity())
                .build();
        productRepository.save(product);
        log.info("Product Added!");
        return product.getProductId();
    }

    @Override
    public List<ProductResponse> fetchAllProducts() {
          List<Product> list = productRepository.findAll();
         List<ProductResponse> listOfProduct = list.stream().map(product -> {
               return ProductResponse.builder()
                      .productName(product.getProductName())
                      .productId(product.getProductId())
                      .price(product.getPrice())
                      .quantity(product.getQuantity())
                      .build();
          }).collect(Collectors.toList());
        return listOfProduct;
    }

    @Override
    @SneakyThrows
    public ProductResponse getProductById(Long productId) {
       Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException(" Product With id: "+productId+" not found!", "NOT_FOUND"));
       ProductResponse productResponse = new ProductResponse();
        BeanUtils.copyProperties(product,productResponse);
        //Bean Utils copy properties only works when both the source and target object have the properties with same name
        return productResponse;
    }

    @Override
    @SneakyThrows
    public void updateQuantity(long productId, long quantity) {
        log.info("Update Quantity {} for productId {}",quantity,productId);
        Product product = productRepository.findById(productId).orElseThrow(()-> new ResourceNotFoundException("Product with id: "+productId+" not found!","NOT_FOUND"));
        if(product.getQuantity() < quantity){
            log.info("Exception occurred !!");
            throw new ProductNotAvailableException("Product does not have sufficient quantity.","INSUFFICIENT_QUANTITY");
        }
        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);
        log.info("Product Quantity updated successfully!");
    }
}
