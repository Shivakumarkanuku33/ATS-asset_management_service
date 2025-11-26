package com.ats.assetservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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


@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER')")
    public ResponseEntity<AssetResponse> createAsset(@RequestBody AssetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(assetService.createAsset(request));
    }

    @GetMapping("/{id}")
    public AssetResponse getAsset(@PathVariable Long id) {
        return assetService.getAsset(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER')")
    public AssetResponse updateAsset(@PathVariable Long id, @RequestBody AssetRequest request) {
        return assetService.updateAsset(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER')")
    public ResponseEntity<?> deleteAsset(@PathVariable Long id) {
        assetService.deleteOrRetireAsset(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER')")
    public AssetResponse updateStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest request) {
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



