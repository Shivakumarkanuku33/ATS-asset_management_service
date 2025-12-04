package com.ats.assetservice.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ats.assetservice.dto.LocationResponse;
import com.ats.assetservice.dto.VendorResponse;

@FeignClient(name = "location-service", url = "${services.location.url}")
public interface LocationServiceClient {

	 @GetMapping("/locations/{id}")
	    LocationResponse getLocationById(@PathVariable Long id);

	    @GetMapping("/vendors/{id}")
	    VendorResponse getVendorById(@PathVariable Long id);
}
