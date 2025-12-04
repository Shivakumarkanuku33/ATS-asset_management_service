package com.ats.assetservice.dto;

import lombok.Data;

@Data
public class VendorResponse {
	private Long id;
	private String vendorName;
	private String contactPerson;
	private String phone;
	private String email;
	private String status;
}
