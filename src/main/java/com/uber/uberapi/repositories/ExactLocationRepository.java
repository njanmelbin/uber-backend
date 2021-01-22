package com.uber.uberapi.repositories;

import com.uber.uberapi.models.ExactLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExactLocationRepository extends JpaRepository<ExactLocation,Long> {
}
