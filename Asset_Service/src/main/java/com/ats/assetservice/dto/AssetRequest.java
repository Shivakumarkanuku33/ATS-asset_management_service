package com.ats.assetservice.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AssetRequest {
    private String name;
    private String category;
    private String serialNumber;
    private Long vendorId;
    private LocalDate purchaseDate;
    private Double purchaseCost;
    private LocalDate warrantyExpiry;
    private Long locationId;  // temporarily ignored
    private String notes;
}
