package com.project.paymentservice.service;

import com.project.paymentservice.entity.TransactionDetails;
import com.project.paymentservice.model.PaymentMode;
import com.project.paymentservice.model.PaymentRequest;
import com.project.paymentservice.model.PaymentResponse;
import com.project.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{
    private final PaymentRepository paymentRepository;
    @Override
    public Long makePayment(PaymentRequest paymentRequest) {
        log.info("Recording payment details : {}",paymentRequest);
        TransactionDetails transactionDetails = TransactionDetails.builder()
                .paymentDate(Instant.now())
                .paymentMode(paymentRequest.getPaymentMode().name())
                .amount(paymentRequest.getAmount())
                .referenceNo(paymentRequest.getReferenceNo())
                .orderId(paymentRequest.getOrderId())
                .paymentStatus("SUCCESS").build();
        paymentRepository.save(transactionDetails);
        log.info("Transaction Completed with Id : {}",transactionDetails.getId());
        return transactionDetails.getId();
    }

    @Override
    public PaymentResponse getPaymentDetailsByOrderId(long orderId) {
        log.info("Getting payment details for order id {}",orderId);
        TransactionDetails transactionDetails = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order with orderId :"+orderId+" does not exist."));
        PaymentResponse paymentResponse = PaymentResponse.builder()
                .paymentId(transactionDetails.getId())
                .orderId(transactionDetails.getOrderId())
                .paymentMode(PaymentMode.valueOf(transactionDetails.getPaymentMode()))
                .paymentDate(transactionDetails.getPaymentDate())
                .amount(transactionDetails.getAmount())
                .status(transactionDetails.getPaymentStatus())
                .build();
        return paymentResponse;
    }
}
