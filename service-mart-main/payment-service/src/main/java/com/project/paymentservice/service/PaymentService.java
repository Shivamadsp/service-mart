package com.project.paymentservice.service;

import com.project.paymentservice.model.PaymentRequest;
import com.project.paymentservice.model.PaymentResponse;

public interface PaymentService {
    Long makePayment(PaymentRequest paymentRequest);

    PaymentResponse getPaymentDetailsByOrderId(long orderId);
}
