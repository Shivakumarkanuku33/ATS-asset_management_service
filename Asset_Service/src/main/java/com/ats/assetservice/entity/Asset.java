package com.ats.assetservice.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Builder
@Entity
@Table(name = "assets_tbl",uniqueConstraints = @UniqueConstraint(columnNames = "asset_tag"))
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "asset_tag", nullable = false, unique = true)
    private String assetTag;

    private String name;
    private String category;
    private String status;
    private LocalDate purchaseDate;
    private Double purchaseCost;
    private String vendor;
    private Integer warrantyMonths;
    private String description;
    
    private Long locationId;

    private Boolean isActive = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
