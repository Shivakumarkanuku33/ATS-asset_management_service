package com.ats.assetservice.dto;

import java.time.LocalDate;

import com.ats.assetservice.entity.AssetStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetResponse {
    private Long assetId;
    private String name;
    private String serialNumber;
    private String category;
    private AssetStatus status;
    private LocationResponse location; // will be null until location service implemented
    private VendorResponse vendor;
    private AssignmentResponse currentAssignment;
    private LocalDate purchaseDate;


}