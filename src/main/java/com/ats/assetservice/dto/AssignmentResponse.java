package com.ats.assetservice.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentResponse {

	private Long id;
	private Long assetId;
	private Long employeeId;
	private LocalDate assignedDate;
	private LocalDate dueDate;
//	private LocalDate returnedDate;
	private String status;
	private String notes;
//	private String condition;
}
