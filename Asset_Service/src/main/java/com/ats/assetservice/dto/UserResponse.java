package com.ats.assetservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Long userId;
    private String username;
    private String email;
    private String name;
    private String department;
    private String role;
    private Long employeeId;
    private String status; // must match UserService JSON
}
