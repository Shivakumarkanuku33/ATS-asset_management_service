package com.ats.assetservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ats.assetservice.entity.AssetAuditLog;

public interface AssetAuditLogRepository extends JpaRepository<AssetAuditLog, Long>{

	List<AssetAuditLog> findByAssetId(Long assetId);
}
