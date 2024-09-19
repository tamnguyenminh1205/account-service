package com.ojt.klb.service;

import com.ojt.klb.dto.AccountDto;
import com.ojt.klb.dto.ChangeStatusDto;
import com.ojt.klb.dto.FindByAccountDto;

import java.util.Optional;

public interface AccountService {
    Optional<AccountDto> getAccountById(Long id);
    void changeStatusAccount(Long id , ChangeStatusDto changeStatusDto);
    Optional<Long> getAccountIdByAccountNumber(Long accountNumber);
    String getFullNameByAccountId(Long accountId);
}
