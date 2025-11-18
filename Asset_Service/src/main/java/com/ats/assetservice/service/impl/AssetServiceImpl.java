package com.ats.assetservice.service.impl;

import com.ats.assetservice.dto.AssetRequest;
import com.ats.assetservice.dto.AssetResponse;
import com.ats.assetservice.entity.Asset;
import com.ats.assetservice.entity.AssetStatus;
import com.ats.assetservice.exception.ResourceAlreadyExistsException;
import com.ats.assetservice.exception.ResourceNotFoundException;
import com.ats.assetservice.mapper.AssetMapper;
import com.ats.assetservice.repository.AssetRepository;
import com.ats.assetservice.service.AssetService;
import com.ats.assetservice.service.AuditLogService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

	private final AssetRepository assetRepository;
    private final AssetMapper mapper;
    private final AuditLogService auditLogService;

	@Override
	public AssetResponse createAsset(AssetRequest request) {

		Asset asset = mapper.toEntity(request);
		
		// Auto-generate unique assetTag
	    asset.setAssetTag("AST-" + UUID.randomUUID().toString().substring(0, 8));

//		asset.setStatus("AVAILABLE");
	    asset.setAssetStatus(AssetStatus.AVAILABLE);

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
		Page<Asset> result = assetRepository.findAll(pageable); // fetch all assets
//	    return result.map(mapper::toResponse);
//		Page<Asset> result;

//		Specification<Asset> spec = Specification.where(null);
		
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

//	@Override
//	public AssetResponse changeStatus(Long id, String newStatus) {
//	    Asset asset = assetRepository.findById(id)
//	            .orElseThrow(() -> new ResourceNotFoundException("Asset not found"));
//	    String oldStatus = asset.getStatus();
//	    asset.setStatus(newStatus);
//	    assetRepository.save(asset);
//
//	    // Log the status change
//	    auditLogService.logUpdate(asset, AssetRequest.builder().status(newStatus).build());
//	    return mapper.toResponse(asset);
//	}

	@Override
    @Transactional
    public Asset updateAssetStatus(Long assetId, String status, String description) {
        Optional<Asset> assetOpt = assetRepository.findById(assetId);
        if (assetOpt.isEmpty()) {
            throw new IllegalArgumentException("Asset not found with id: " + assetId);
        }

        Asset asset = assetOpt.get();

        try {
            AssetStatus newStatus = AssetStatus.valueOf(status.trim().toUpperCase());
            asset.setStatus(newStatus.name());
            asset.setUpdatedAt(java.time.LocalDateTime.now());

            assetRepository.save(asset);

            // Log lifecycle/status change
            auditLogService.logStatusChange(asset, description);

            return asset;

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid status. Allowed values: AVAILABLE, IN_USE, UNDER_MAINTENANCE, RETIRED"
            );
        }
    }
}