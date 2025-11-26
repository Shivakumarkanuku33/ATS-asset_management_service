package com.ats.assetservice.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.ats.assetservice.dto.UserResponse;

//@FeignClient(name = "user-service", url = "${user.service.url}")
//@FeignClient(name = "user-service", url = "${user.service.url}")
@FeignClient(name = "user-service", url = "${user.service.url}", configuration = FeignConfig.class)
public interface UserClient {
    @GetMapping("/users/me")
    UserResponse getCurrentUser(@RequestHeader("Authorization") String authHeader);
}

//@FeignClient(name = "user-service", url = "${user.service.url}", configuration = FeignConfig.class)
//public interface UserClient {
//    @GetMapping("/users/me")
//    UserResponse getCurrentUser();
//}