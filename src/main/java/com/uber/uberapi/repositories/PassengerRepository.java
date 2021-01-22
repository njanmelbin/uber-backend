package com.uber.uberapi.repositories;

import com.uber.uberapi.models.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerRepository extends JpaRepository<Passenger,Long> {
}
