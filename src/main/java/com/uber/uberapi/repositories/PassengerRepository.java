package com.uber.uberapi.repositories;

import com.uber.uberapi.models.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PassengerRepository extends JpaRepository<Passenger,Long> {
    Optional<Passenger> findFirstByAccount_Username(String username);
}
