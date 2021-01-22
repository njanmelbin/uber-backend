package com.uber.uberapi.repositories;

import com.uber.uberapi.models.Color;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColorRepository extends JpaRepository<Color,Long> {
}
