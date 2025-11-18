package com.ats.assetservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusUpdateRequest {

	private String status;
	private String reason;
}
