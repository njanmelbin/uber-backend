package com.uber.uberapi.repositories;

import com.uber.uberapi.models.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car,Long> {
}
