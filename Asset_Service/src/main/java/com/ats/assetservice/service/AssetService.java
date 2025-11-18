package com.ats.assetservice.service;

import com.ats.assetservice.dto.AssetRequest;
import com.ats.assetservice.dto.AssetResponse;
import com.ats.assetservice.entity.Asset;

import org.springframework.data.domain.Page;

public interface AssetService {

    AssetResponse createAsset(AssetRequest request);

    AssetResponse updateAsset(Long id, AssetRequest request);

    AssetResponse getAsset(Long id);

    Page<AssetResponse> getAllAssets(int page, int size, String status, String category);

    void softDelete(Long id);
    
    void hardDelete(Long id);
        
    Asset updateAssetStatus(Long assetId, String status, String description);
}
