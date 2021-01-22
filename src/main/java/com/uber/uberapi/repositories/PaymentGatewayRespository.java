package com.uber.uberapi.repositories;

import com.uber.uberapi.models.PaymentGateway;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentGatewayRespository extends JpaRepository<PaymentGateway,Long> {
}
