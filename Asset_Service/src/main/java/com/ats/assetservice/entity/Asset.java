package com.ats.assetservice.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "assets")
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String category;
    @Column(unique = true)
    private String serialNumber;

    @Enumerated(EnumType.STRING)
    private AssetStatus status;

//    @ManyToOne
//    @JoinColumn(name = "vendor_id")
//    private Vendor vendor;

//    @ManyToOne
//    @JoinColumn(name = "location_id")
//    private Object location; // temporary null

    private LocalDate purchaseDate;
    private Double purchaseCost;
    private LocalDate warrantyExpiry;
    private String notes;
}
