package com.project.paymentservice.repository;

import com.project.paymentservice.entity.TransactionDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<TransactionDetails,Long> {
    Optional<TransactionDetails> findByOrderId(long orderId);
}
