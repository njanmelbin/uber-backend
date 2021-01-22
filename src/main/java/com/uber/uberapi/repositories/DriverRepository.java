package com.uber.uberapi.repositories;

import com.uber.uberapi.models.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver,Long> {
}
