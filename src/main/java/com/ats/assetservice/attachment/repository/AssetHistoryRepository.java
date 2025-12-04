package com.ats.assetservice.attachment.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.ats.assetservice.attachment.entity.AssetHistory;

import java.util.List;

public interface AssetHistoryRepository extends JpaRepository<AssetHistory, Long> {
    List<AssetHistory> findByAssetIdOrderByPerformedAtDesc(Long assetId);
}
