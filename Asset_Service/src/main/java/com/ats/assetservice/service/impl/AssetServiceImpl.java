package com.ats.assetservice.service.impl;

import com.ats.assetservice.dto.AssetRequest;
import com.ats.assetservice.dto.AssetResponse;
import com.ats.assetservice.entity.Asset;
import com.ats.assetservice.exception.ResourceAlreadyExistsException;
import com.ats.assetservice.exception.ResourceNotFoundException;
import com.ats.assetservice.mapper.AssetMapper;
import com.ats.assetservice.repository.AssetRepository;
import com.ats.assetservice.service.AssetService;
import com.ats.assetservice.service.AuditLogService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

	private final AssetRepository assetRepository;
    private final AssetMapper mapper;
    private final AuditLogService auditLogService;

	@Override
	public AssetResponse createAsset(AssetRequest request) {

//		if (assetRepository.existsByAssetTag(request.getAssetTag())) {
//			throw new ResourceAlreadyExistsException("Asset tag already exists");
//		}

		Asset asset = mapper.toEntity(request);
		
		// Auto-generate unique assetTag
	    asset.setAssetTag("AST-" + UUID.randomUUID().toString().substring(0, 8));

		asset.setStatus("AVAILABLE");
		asset.setCreatedAt(LocalDateTime.now());
		asset.setUpdatedAt(LocalDateTime.now());

		Asset saved = assetRepository.save(asset);

		auditLogService.logCreate(saved);

		return mapper.toResponse(saved);
	}

	@Override
	public AssetResponse updateAsset(Long id, AssetRequest request) {

		Asset asset = assetRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Asset not found"));

		auditLogService.logUpdate(asset, request);

		mapper.updateEntityFromRequest(request, asset);
		asset.setUpdatedAt(LocalDateTime.now());

		return mapper.toResponse(assetRepository.save(asset));
	}

	@Override
	public AssetResponse getAsset(Long id) {
		Asset asset = assetRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Asset not found"));

		return mapper.toResponse(asset);
	}

	@Override
	public Page<AssetResponse> getAllAssets(int page, int size, String status, String category) {

		Pageable pageable = PageRequest.of(page, size);
		Page<Asset> result;

		if (status != null && category != null) {
			result = assetRepository.findAll((root, query, builder) -> builder
					.and(builder.equal(root.get("status"), status), builder.equal(root.get("category"), category)),
					pageable);
		} else {
			result = assetRepository.findAll(pageable);
		}

		return result.map(mapper::toResponse);
	}

	@Override
	public void softDelete(Long id) {

		Asset asset = assetRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Asset not found"));

		asset.setIsActive(false);
		asset.setUpdatedAt(LocalDateTime.now());

		auditLogService.logDelete(asset);

		assetRepository.save(asset);
	}

	@Override
	public void hardDelete(Long id) {
		Asset asset = assetRepository.findById(id)
	            .orElseThrow(() -> new ResourceNotFoundException("Asset not found"));

	    auditLogService.logDelete(asset); // Optional: log the deletion

	    assetRepository.delete(asset); 
		
	}
	
	
// System Generated assetTag
//	@Override
//	public AssetResponse createAssetResponse(AssetRequest request) {
//		
//		Asset asset = mapper.toEntity(request);
//
//	    // Auto-generate assetTag
//		asset.setAssetTag("AST-" + UUID.randomUUID().toString().substring(0, 8));
//		
//
//	    asset.setStatus("AVAILABLE");
//	    asset.setCreatedAt(LocalDateTime.now());
//	    asset.setUpdatedAt(LocalDateTime.now());
//
//	    Asset saved = assetRepository.save(asset);
//
//	    auditLogService.logCreate(saved);
//
//	    return mapper.toResponse(saved);
//	}
}