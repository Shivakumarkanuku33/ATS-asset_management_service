package com.ats.assetservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ats.assetservice.dto.AssetRequest;
import com.ats.assetservice.dto.AssetResponse;
import com.ats.assetservice.dto.PaginatedResponse;
import com.ats.assetservice.dto.StatusUpdateRequest;
import com.ats.assetservice.service.AssetService;
import com.ats.assetservice.service.impl.UserValidationService;

import lombok.RequiredArgsConstructor;

//import com.ats.assetservice.dto.AssetRequest;
//import com.ats.assetservice.dto.AssetResponse;
//import com.ats.assetservice.dto.StatusUpdateRequest;
//import com.ats.assetservice.entity.Asset;
//import com.ats.assetservice.service.AssetService;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import jakarta.validation.Valid;
//
//@RestController
//@RequestMapping("/api/assets")
//@RequiredArgsConstructor
//public class AssetController {
//
//    private final AssetService assetService;
//
//    @PostMapping
//    public ResponseEntity<AssetResponse> create(@Valid @RequestBody AssetRequest request) {
//        return ResponseEntity.ok(assetService.createAsset(request));
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<AssetResponse> getById(@PathVariable Long id) {
//        return ResponseEntity.ok(assetService.getAsset(id));
//    }
//
//    @GetMapping
//    public ResponseEntity<Page<AssetResponse>> getAll(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(required = false) String status,
//            @RequestParam(required = false) String category)
//    {
//        return ResponseEntity.ok(assetService.getAllAssets(page, size, status, category));
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<AssetResponse> update(
//            @PathVariable Long id,
//            @Valid @RequestBody AssetRequest request) {
//
//        return ResponseEntity.ok(assetService.updateAsset(id, request));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<String> softDelete(@PathVariable Long id) {
//        assetService.softDelete(id);
//        return ResponseEntity.ok("Asset deleted successfully");
//    }
//    @DeleteMapping("/hard/{id}")
//    public ResponseEntity<String> hardDelete(@PathVariable Long id) {
//        assetService.hardDelete(id);
//        return ResponseEntity.ok("Asset permanently deleted successfully cannot be recover");
//    }
//    
//    @PatchMapping("/{id}/status")
//    public ResponseEntity<?> updateAssetStatus(
//            @PathVariable Long id,
//            @RequestBody StatusUpdateRequest request) {
//        try {
//            Asset updated = assetService.updateAssetStatus(id, request.getStatus(), request.getReason());
//            return ResponseEntity.ok(updated);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//    
//    }


@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;
    private final UserValidationService userValidationService;

    @PostMapping
    public ResponseEntity<AssetResponse> createAsset(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody AssetRequest request) {

        userValidationService.validateUserRole(authHeader);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(assetService.createAsset(request));
    }

    @GetMapping("/{id}")
    public AssetResponse getAsset(@PathVariable Long id) {
        return assetService.getAsset(id);
    }

    @PutMapping("/{id}")
    public AssetResponse updateAsset(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @RequestBody AssetRequest request) {

        userValidationService.validateUserRole(authHeader);
        return assetService.updateAsset(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAsset(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {

        userValidationService.validateUserRole(authHeader);
        assetService.deleteOrRetireAsset(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public AssetResponse updateStatus(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest request) {

        userValidationService.validateUserRole(authHeader);
        return assetService.updateStatus(id, request);
    }
    
    @GetMapping("/allAssets")
    public ResponseEntity<PaginatedResponse<List<AssetResponse>>> getAllAssets(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String serialNumber,
            @RequestParam(required = false) Long locationId,
            @RequestParam(required = false) Long vendorId
    ) {
        PaginatedResponse<List<AssetResponse>> response =
                assetService.getAllAssets(page, size, category, status, serialNumber, locationId, vendorId);

        return ResponseEntity.ok(response);
    }
}



