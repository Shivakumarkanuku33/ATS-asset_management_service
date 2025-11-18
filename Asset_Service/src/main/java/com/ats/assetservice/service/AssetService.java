package com.ats.assetservice.service;

import com.ats.assetservice.dto.AssetRequest;
import com.ats.assetservice.dto.AssetResponse;
import org.springframework.data.domain.Page;

public interface AssetService {

    AssetResponse createAsset(AssetRequest request);

    AssetResponse updateAsset(Long id, AssetRequest request);

    AssetResponse getAsset(Long id);

    Page<AssetResponse> getAllAssets(int page, int size, String status, String category);

    void softDelete(Long id);
    
    void hardDelete(Long id);
    
    AssetResponse changeStatus(Long id, String newStatus);
}
