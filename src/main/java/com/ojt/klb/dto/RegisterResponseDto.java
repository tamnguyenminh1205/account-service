package com.ojt.klb.dto;

import lombok.Data;

@Data
public class RegisterResponseDto {
    private Long userId;
    private String username;
    private String role;
}
