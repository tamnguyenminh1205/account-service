package com.ojt.klb.repository;

import com.ojt.klb.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByPhoneNumber(String phoneNumber);
}
