package com.uber.uberapi.repositories;

import com.uber.uberapi.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review,Long> {
}
