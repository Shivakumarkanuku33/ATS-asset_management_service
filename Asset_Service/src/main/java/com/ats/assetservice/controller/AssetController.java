package com.ats.assetservice.controller;

import com.ats.assetservice.dto.AssetRequest;
import com.ats.assetservice.dto.AssetResponse;
import com.ats.assetservice.dto.StatusUpdateRequest;
import com.ats.assetservice.entity.Asset;
import com.ats.assetservice.service.AssetService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @PostMapping
    public ResponseEntity<AssetResponse> create(@Valid @RequestBody AssetRequest request) {
        return ResponseEntity.ok(assetService.createAsset(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssetResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(assetService.getAsset(id));
    }

    @GetMapping
    public ResponseEntity<Page<AssetResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category)
    {
        return ResponseEntity.ok(assetService.getAllAssets(page, size, status, category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssetResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody AssetRequest request) {

        return ResponseEntity.ok(assetService.updateAsset(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> softDelete(@PathVariable Long id) {
        assetService.softDelete(id);
        return ResponseEntity.ok("Asset deleted successfully");
    }
    @DeleteMapping("/hard/{id}")
    public ResponseEntity<String> hardDelete(@PathVariable Long id) {
        assetService.hardDelete(id);
        return ResponseEntity.ok("Asset permanently deleted successfully cannot be recover");
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateAssetStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest request) {
        try {
            Asset updated = assetService.updateAssetStatus(id, request.getStatus(), request.getReason());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    }


