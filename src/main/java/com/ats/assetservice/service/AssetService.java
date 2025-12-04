package com.ats.assetservice.service;

import java.util.List;

import com.ats.assetservice.dto.AssetRequest;
import com.ats.assetservice.dto.AssetResponse;
import com.ats.assetservice.dto.PaginatedResponse;
import com.ats.assetservice.dto.StatusUpdateRequest;

//import com.ats.assetservice.dto.AssetRequest;
//import com.ats.assetservice.dto.AssetResponse;
//import com.ats.assetservice.entity.Asset;
//
//import org.springframework.data.domain.Page;
//
//public interface AssetService {
//
//    AssetResponse createAsset(AssetRequest request);
//
//    AssetResponse updateAsset(Long id, AssetRequest request);
//
//    AssetResponse getAsset(Long id);
//
//    Page<AssetResponse> getAllAssets(int page, int size, String status, String category);
//
//    void softDelete(Long id);
//    
//    void hardDelete(Long id);
//        
//    Asset updateAssetStatus(Long assetId, String status, String description);
//}



public interface AssetService {
    AssetResponse createAsset(AssetRequest request);
    AssetResponse getAsset(Long id);
    AssetResponse updateAsset(Long id, AssetRequest request);
    void deleteOrRetireAsset(Long id);
    AssetResponse updateStatus(Long id, StatusUpdateRequest request);
    
    PaginatedResponse<List<AssetResponse>> getAllAssets(
            int page,
            int size,
            String category,
            String status,
            String serialNumber,
            Long locationId,
            Long vendorId
    );
}
