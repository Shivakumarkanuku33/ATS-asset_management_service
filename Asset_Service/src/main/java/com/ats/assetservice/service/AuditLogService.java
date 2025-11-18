package com.ats.assetservice.service;

import com.ats.assetservice.dto.AssetRequest;
import com.ats.assetservice.entity.Asset;

public interface AuditLogService {

	void logCreate(Asset asset);

    void logUpdate(Asset oldAsset, AssetRequest newData);

    void logDelete(Asset asset);
    
    void logStatusChange(Asset asset, String reason);
}
