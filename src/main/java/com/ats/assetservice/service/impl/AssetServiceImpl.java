package com.ats.assetservice.service.impl;

//import java.awt.print.Pageable;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.ats.assetservice.dto.AssetRequest;
import com.ats.assetservice.dto.AssetResponse;
import com.ats.assetservice.dto.AssignmentResponse;
import com.ats.assetservice.dto.LocationResponse;
import com.ats.assetservice.dto.PaginatedResponse;
import com.ats.assetservice.dto.StatusUpdateRequest;
import com.ats.assetservice.dto.VendorResponse;
import com.ats.assetservice.entity.Asset;
import com.ats.assetservice.entity.AssetStatus;
import com.ats.assetservice.feignclient.AssignmentClient;
import com.ats.assetservice.feignclient.LocationServiceClient;
import com.ats.assetservice.repository.AssetRepository;
import com.ats.assetservice.service.AssetService;
import com.ats.assetservice.specification.AssetSpecification;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j

@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

	private final AssetRepository assetRepository;
//    private final VendorRepository vendorRepository;
	private final ModelMapper modelMapper;
	
	private final HttpServletRequest request;


	private final LocationServiceClient locationServiceClient;
	private final AssignmentClient assignmentClient;

	@Override
	public AssetResponse createAsset(AssetRequest request) {
		if (assetRepository.existsBySerialNumber(request.getSerialNumber()))
			throw new RuntimeException("Serial number exists");

		Asset asset = Asset.builder().name(request.getName()).serialNumber(request.getSerialNumber())
				.category(request.getCategory()).vendorId(request.getVendorId()).locationId(request.getLocationId())
				.status(AssetStatus.AVAILABLE).purchaseDate(request.getPurchaseDate())
				.purchaseCost(request.getPurchaseCost()).warrantyExpiry(request.getWarrantyExpiry())
				.notes(request.getNotes()).build();

		assetRepository.save(asset);
		return mapToResponse(asset);
	}

	@Override
	public AssetResponse getAsset(Long id) {
		Asset asset = assetRepository.findById(id).orElseThrow(() -> new RuntimeException("Asset not found"));
		return mapToResponse(asset);
	}

	@Override
	public AssetResponse updateAsset(Long id, AssetRequest request) {
		Asset asset = assetRepository.findById(id).orElseThrow(() -> new RuntimeException("Asset not found"));

		if (request.getName() != null)
			asset.setName(request.getName());
		if (request.getCategory() != null)
			asset.setCategory(request.getCategory());
		if (request.getSerialNumber() != null)
			asset.setSerialNumber(request.getSerialNumber());
		if (request.getVendorId() != null)
			asset.setVendorId(request.getVendorId());
		if (request.getLocationId() != null)
			asset.setLocationId(request.getLocationId());
		if (request.getPurchaseDate() != null)
			asset.setPurchaseDate(request.getPurchaseDate());
		if (request.getPurchaseCost() != null)
			asset.setPurchaseCost(request.getPurchaseCost());
		if (request.getWarrantyExpiry() != null)
			asset.setWarrantyExpiry(request.getWarrantyExpiry());
		if (request.getNotes() != null)
			asset.setNotes(request.getNotes());

		assetRepository.save(asset);
		return mapToResponse(asset);
	}

	@Override
	public void deleteOrRetireAsset(Long id) {
		Asset asset = assetRepository.findById(id).orElseThrow(() -> new RuntimeException("Asset not found"));
		if (asset.getStatus() == AssetStatus.ASSIGNED)
			throw new RuntimeException("Cannot delete assigned asset");

		asset.setStatus(AssetStatus.RETIRED);
		assetRepository.save(asset);
	}

	@Override
	public AssetResponse updateStatus(Long id, StatusUpdateRequest request) {
		Asset asset = assetRepository.findById(id).orElseThrow(() -> new RuntimeException("Asset not found"));
		asset.setStatus(AssetStatus.valueOf(request.getStatus()));
		assetRepository.save(asset);
		return mapToResponse(asset);
	}

	// Changes from here
	private AssetResponse mapToResponse(Asset asset) {

		log.info("Mapping asset: {}", asset.getId());
		LocationResponse location = null;
		VendorResponse vendor = null;
		AssignmentResponse assignment = null;
		
		String token = request.getHeader("Authorization");

		// Fetch Location
		if (asset.getLocationId() != null) {
			try {
				location = locationServiceClient.getLocationById(asset.getLocationId());
			} catch (Exception ignored) {
				location = null;
			}
		}

		// Fetch Vendor
		if (asset.getVendorId() != null) {
			try {
				vendor = locationServiceClient.getVendorById(asset.getVendorId());
			} catch (Exception ignored) {
				vendor = null;
			}
			
		}
		
		// Fetch Assignment (ALWAYS TRY – DO NOT CHECK assignmentId)
		try {
		    assignment = assignmentClient.getAssignmentByAssetId(token, asset.getId());
		    log.info("ASSIGNMENT FROM ASSIGNMENT-SERVICE = {}", assignment);
		} catch (Exception ex) {
		    log.error("Error calling assignment service", ex);
		}
	    

	    return AssetResponse.builder()
	            .assetId(asset.getId())
	            .name(asset.getName())
	            .serialNumber(asset.getSerialNumber())
	            .category(asset.getCategory())
	            .status(asset.getStatus())
	            .location(location)
	            .vendor(vendor)
	            .purchaseDate(asset.getPurchaseDate())
	            .currentAssignment(assignment)
	            .build();
	}

	@Override
	public PaginatedResponse<List<AssetResponse>> getAllAssets(int page, int size, String category, String status,
			String serialNumber, Long locationId, Long vendorId) {

		Pageable pageable = PageRequest.of(page - 1, size);

		Specification<Asset> spec = AssetSpecification.filterAssets(category, status, serialNumber, locationId,
				vendorId);

		Page<Asset> assetPage = assetRepository.findAll(spec, pageable);

		List<AssetResponse> assetResponses = assetPage.getContent().stream()
//                .map(asset -> modelMapper.map(asset, AssetResponse.class))
				.map(this::mapToResponse).collect(Collectors.toList());

		return PaginatedResponse.<List<AssetResponse>>builder().status("success").data(assetResponses)
				.meta(PaginatedResponse.Meta.builder().page(page).size(size).totalElements(assetPage.getTotalElements())
						.totalPages(assetPage.getTotalPages()).build())
				.build();
	}
}
