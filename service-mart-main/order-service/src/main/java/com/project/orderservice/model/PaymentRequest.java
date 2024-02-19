package com.project.orderservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private long orderId;
    private long amount;
    private String referenceNo;
    private PaymentMode paymentMode;
}
