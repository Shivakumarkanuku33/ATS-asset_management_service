package com.ats.assetservice.service.impl;

import org.springframework.stereotype.Service;

import com.ats.assetservice.dto.UserResponse;
import com.ats.assetservice.feignclient.UserClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserValidationService {
    private final UserClient userClient;

    public UserResponse validateUserRole(String authHeader) {
        UserResponse user = userClient.getCurrentUser(authHeader);
        if (!"ADMIN".equals(user.getRole()) && !"ASSET_MANAGER".equals(user.getRole())) {
            throw new RuntimeException("User not authorized to create/update asset");
        }
        return user;
    }
}
