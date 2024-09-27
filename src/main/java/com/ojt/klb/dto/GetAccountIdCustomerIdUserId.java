package com.ojt.klb.dto;

import lombok.Data;

@Data
public class GetAccountIdCustomerIdUserId {
    private Long userId;
    private Long accountId;
    private Long customerId;
}
