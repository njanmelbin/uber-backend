package com.uber.uberapi.repositories;

import com.uber.uberapi.models.Constant;
import org.hibernate.secure.spi.JaccPermissionDeclarations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConstantsRepository extends JpaRepository<Constant,Long> {
}
