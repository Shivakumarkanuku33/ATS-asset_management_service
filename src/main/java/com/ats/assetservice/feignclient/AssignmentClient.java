package com.ats.assetservice.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.ats.assetservice.dto.AssignmentPageResponse;
import com.ats.assetservice.dto.AssignmentResponse;

@FeignClient(name = "assignment-service", url = "${assignment.service.url}")
public interface AssignmentClient {

    @GetMapping("/assignments/asset/{assetId}")
    AssignmentResponse getAssignmentByAssetId(@RequestHeader("Authorization") String token,@PathVariable Long assetId);
}
