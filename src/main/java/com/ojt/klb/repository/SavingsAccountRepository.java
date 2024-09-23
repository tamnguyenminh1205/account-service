package com.ojt.klb.repository;

import com.ojt.klb.model.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, Long> {
    boolean existsByUserId(Long userId);

}
