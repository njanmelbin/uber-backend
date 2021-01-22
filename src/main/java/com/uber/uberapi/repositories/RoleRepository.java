package com.uber.uberapi.repositories;

import com.uber.uberapi.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {
}
