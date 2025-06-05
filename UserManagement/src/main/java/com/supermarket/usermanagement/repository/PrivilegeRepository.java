package com.supermarket.usermanagement.repository;

import com.supermarket.usermanagement.model.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PrivilegeRepository extends JpaRepository<Privilege, UUID> {

    Optional<Privilege> findByName(String name);

    boolean existsByName(String name);
}