package com.ojt.klb.service;

import com.ojt.klb.dto.LoginDto;
import com.ojt.klb.dto.RegisterDto;

import java.util.Optional;

public interface UserService {
    Optional<LoginDto> login(String username, String password);

    void createUser (RegisterDto registerDto);
    void forgetPassword (Long userId, String newPassword);
}
