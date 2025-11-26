package com.ats.assetservice.attachment.service;

import org.springframework.stereotype.Service;

import com.ats.assetservice.attachment.dto.HistoryEntryDto;
import com.ats.assetservice.attachment.repository.AssetHistoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistoryService {
    private final AssetHistoryRepository repo;

    public HistoryService(AssetHistoryRepository repo) { this.repo = repo; }

    public List<HistoryEntryDto> getHistoryForAsset(Long assetId) {
        return repo.findByAssetIdOrderByPerformedAtDesc(assetId).stream().map(h -> HistoryEntryDto.builder()
                .id(h.getId())
                .eventType(h.getEventType())
                .summary(h.getSummary())
                .details(h.getDetails())
                .performedBy(h.getPerformedBy())
                .performedAt(h.getPerformedAt())
                .build()).collect(Collectors.toList());
    }
}
