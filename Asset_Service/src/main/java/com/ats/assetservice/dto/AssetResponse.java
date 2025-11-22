package com.ats.assetservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ats.assetservice.entity.AssetStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

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
    private LocationDTO location; // will be null until location service implemented
    private VendorDTO vendor;
    private AssignmentDTO currentAssignment;
    private LocalDate purchaseDate;

    @Data
    @Builder
    public static class LocationDTO { 
        private Long id;
        private String name; 
    }

    @Data
    @Builder
    public static class VendorDTO { 
        private Long id;
        private String name; 
    }

    @Data
    @Builder
    public static class AssignmentDTO { 
        private Long assignmentId;
        private String assignedTo;
        private String assignedDate;
    }
}