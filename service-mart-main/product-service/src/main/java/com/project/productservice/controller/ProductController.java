package com.project.productservice.controller;
import com.project.productservice.model.ProductRequest;
import com.project.productservice.model.ProductResponse;
import com.project.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PreAuthorize("hasAuthority('Admin')")
    @PostMapping("/add")
    public ResponseEntity<Long> addProducts(@RequestBody ProductRequest productRequest){
        long product_id = productService.addProduct(productRequest);
        return new ResponseEntity<>(product_id,HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('Admin')")
    @GetMapping("/all")
    public ResponseEntity<List<ProductResponse>> getAllProducts(){
        List<ProductResponse> listOfProductResponse = productService.fetchAllProducts();
        return new ResponseEntity<>(listOfProductResponse,HttpStatus.OK);
    }


    @PreAuthorize("hasAuthority('Admin') || hasAuthority('Customer') || hasAuthority('SCOPE_internal')")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable("id") Long productId){
        ProductResponse productResponse = productService.getProductById(productId);
        return new ResponseEntity<>(productResponse,HttpStatus.OK);
    }

    @PutMapping("/updateQuantity/{id}")
    public ResponseEntity<Void> updateProductQuantity(@PathVariable("id") long productId, @RequestParam("quantity") long quantity){
        productService.updateQuantity(productId,quantity);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
