package com.ats.assetservice.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssetRequest {

//    @NotBlank
//    private String assetTag;

    @NotBlank
    private String name;

    @NotBlank
    private String category;

    private String status;

    private LocalDate purchaseDate;

    private Double purchaseCost;

    private String vendor;

    private Integer warrantyMonths;

    private String description;
}
