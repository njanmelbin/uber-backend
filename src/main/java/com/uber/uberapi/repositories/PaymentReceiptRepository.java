package com.uber.uberapi.repositories;

import com.uber.uberapi.models.PaymentReceipt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentReceiptRepository extends JpaRepository<PaymentReceipt,Long> {
}
