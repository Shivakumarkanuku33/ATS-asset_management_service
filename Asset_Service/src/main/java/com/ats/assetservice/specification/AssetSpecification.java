package com.ats.assetservice.specification;

import com.ats.assetservice.entity.Asset;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class AssetSpecification {

    public static Specification<Asset> filterAssets(
            String category,
            String status,
            String serialNumber,
            Long locationId,
            Long vendorId
    ) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (category != null) {
                predicates.add(cb.equal(root.get("category"), category));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (serialNumber != null) {
                predicates.add(cb.equal(root.get("serialNumber"), serialNumber));
            }

            if (locationId != null) {
                predicates.add(cb.equal(root.get("locationId"), locationId));
            }

            if (vendorId != null) {
                predicates.add(cb.equal(root.get("vendorId"), vendorId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
