package com.ojt.klb.utils;

import com.ojt.klb.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.SecureRandom;

@Service
public class GenerateUniqueAccountNumber {

    private final AccountRepository accountRepository;
    private final SecureRandom random = new SecureRandom();

    @Autowired
    public GenerateUniqueAccountNumber(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public String generate() {
        String accountNumber;
        do {
            accountNumber = new BigInteger(40, random).toString().substring(0, 10);
        } while (accountRepository.existsByAccountNumber(Long.parseLong(accountNumber)));
        return accountNumber;
    }
}
