package com.ojt.klb.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginDto {
    @Size(min = 6, max = 20, message = "{username.size}")
    @NotNull(message = "{username.notBlank}")
    private String username;

    @Size(min = 3, max = 150, message = "{password.size}")
    @NotNull(message = "{password.notBlank}")
    private String password;

    private String role;
}
