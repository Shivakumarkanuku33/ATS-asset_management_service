//package com.ats.assetservice.service.impl;
//
//import com.ats.assetservice.dto.AssetRequest;
//import com.ats.assetservice.entity.Asset;
//import com.ats.assetservice.entity.AssetAuditLog;
//import com.ats.assetservice.repository.AssetAuditLogRepository;
//import com.ats.assetservice.service.AuditLogService;
//import lombok.RequiredArgsConstructor;
//
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//
//@Service
//@RequiredArgsConstructor
//public class AuditLogServiceImpl implements AuditLogService {
//
//	
//	private final AssetAuditLogRepository repository;
//
//	@Override
//	public void logCreate(Asset asset) {
//		// For create, everything is new → field = "ALL"
//		saveLog(asset.getId(), "CREATE", "ALL", null, asset.toString(), "system");
//	}
//
//	@Override
//	public void logUpdate(Asset oldAsset, AssetRequest newData) {
//
//		if (!oldAsset.getName().equals(newData.getName())) {
//            saveLog(oldAsset.getId(), "UPDATE", "name",
//                    oldAsset.getName(), newData.getName(), "system");
//        }
//        if (!oldAsset.getCategory().equals(newData.getCategory())) {
//            saveLog(oldAsset.getId(), "UPDATE", "category",
//                    oldAsset.getCategory(), newData.getCategory(), "system");
//        }
//        
//        if (!oldAsset.getStatus().equals(newData.getStatus())) {
//            saveLog(oldAsset.getId(), "UPDATE", "status",
//                    oldAsset.getStatus(), newData.getStatus(), "system");
//        }
//        
//        if ((oldAsset.getVendor() != null && !oldAsset.getVendor().equals(newData.getVendor()))
//            || (oldAsset.getVendor() == null && newData.getVendor() != null)) {
//            saveLog(oldAsset.getId(), "UPDATE", "vendor",
//                    oldAsset.getVendor(), newData.getVendor(), "system");
//        }
//        
//        if (valueChanged(oldAsset.getDescription(), newData.getDescription())) {
//            saveLog(oldAsset.getId(), "UPDATE", "description",
//                    oldAsset.getDescription(), newData.getDescription(), "system");
//        }
//
//        if (valueChanged(oldAsset.getPurchaseCost(), newData.getPurchaseCost())) {
//            saveLog(oldAsset.getId(), "UPDATE", "purchaseCost",
//                    String.valueOf(oldAsset.getPurchaseCost()),
//                    String.valueOf(newData.getPurchaseCost()),
//                    "system");
//        }
//	}
//        
//        private boolean valueChanged(Object oldValue, Object newValue) {
//            return (oldValue != null && !oldValue.equals(newValue))
//                    || (oldValue == null && newValue != null);
//        }
//
//	@Override
//	public void logDelete(Asset asset) {
//		// For delete, old asset data is saved under oldValue
//		saveLog(asset.getId(), "DELETE", "ALL", asset.toString(), null, "system");
//	}
//
//////	private void saveLog(Long assetId, String action, String field, String oldValue, String newValue, String user) {
//////
//////		try {
//////			
//////			 AssetAuditLog log = AssetAuditLog.builder()
//////		                .assetId(assetId)
//////		                .action(action)
//////		                .fieldName(field)
//////		                .oldValue(oldValue)
//////		                .newValue(newValue)
//////		                .changedBy(user)
//////		                .changedAt(LocalDateTime.now())
//////		                .build();
//////
//////			repository.save(log);
//////			
//////		} catch (Exception e) {
//////			System.err.println("Audit log failed: " + e.getMessage());
//////		}
////		
////	}
//
//	@Override
//    public void logStatusChange(Asset asset, String reason) {
//        // Log the new status
//        saveLog(asset.getId(), "STATUS_CHANGE", "status", null, asset.getStatus(), "system");
//
//        // Optionally log reason separately
//        if (reason != null && !reason.isBlank()) {
//            saveLog(asset.getId(), "STATUS_CHANGE_REASON", "reason", null, reason, "system");
//        }
//    }
//
//    private void saveLog(Long assetId, String action, String field, String oldValue, String newValue, String user) {
//        try {
//            AssetAuditLog log = AssetAuditLog.builder()
//                    .assetId(assetId)
//                    .action(action)
//                    .fieldName(field)
//                    .oldValue(oldValue)
//                    .newValue(newValue)
//                    .changedBy(user)
//                    .changedAt(LocalDateTime.now())
//                    .build();
//            repository.save(log);
//        } catch (Exception e) {
//            System.err.println("Audit log failed: " + e.getMessage());
//        }
//    }
//}
