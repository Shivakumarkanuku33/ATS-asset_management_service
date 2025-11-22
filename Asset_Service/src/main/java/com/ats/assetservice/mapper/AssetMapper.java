package com.ats.assetservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.ats.assetservice.dto.AssetRequest;
import com.ats.assetservice.dto.AssetResponse;
import com.ats.assetservice.entity.Asset;

@Mapper(componentModel = "spring")  // MUST have this
public interface AssetMapper {

//	@Mapping(target = "assetTag", ignore = true)
    Asset toEntity(AssetRequest dto);

//    AssetResponse toResponse(Asset asset);

    void updateEntityFromRequest(AssetRequest request, @MappingTarget Asset asset);
}
