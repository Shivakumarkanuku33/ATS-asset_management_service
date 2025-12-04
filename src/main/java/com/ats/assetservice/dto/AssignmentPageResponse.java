package com.ats.assetservice.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class AssignmentPageResponse {
	private String status;
	private List<AssignmentResponse> data;
	private Map<String, Object> meta;
}