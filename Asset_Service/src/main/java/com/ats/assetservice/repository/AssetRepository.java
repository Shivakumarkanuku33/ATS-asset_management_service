package com.ats.assetservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.ats.assetservice.entity.Asset;

public interface AssetRepository extends JpaRepository<Asset, Long>, JpaSpecificationExecutor<Asset> {

	boolean existsByAssetTag(String assetTag);

	
	Optional<Asset> findByAssetTag(String assetTag);
	
}
