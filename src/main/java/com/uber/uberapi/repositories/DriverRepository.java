package com.uber.uberapi.repositories;

import com.uber.uberapi.models.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver,Long> {
    Optional<Driver> findFirstByAccount_Usern   ame(String username);

    /*
        select * from
        left outer join account
        on driver.account_id = account.id
        where account.username = ?1
     */
}
