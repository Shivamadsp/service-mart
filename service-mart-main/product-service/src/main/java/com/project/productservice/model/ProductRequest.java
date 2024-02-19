package com.project.productservice.model;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ProductRequest {
    private String name;
    private long price;
    private long quantity;
}
