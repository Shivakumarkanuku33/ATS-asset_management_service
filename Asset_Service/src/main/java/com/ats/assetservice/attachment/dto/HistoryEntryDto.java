package com.ats.assetservice.attachment.dto;

import lombok.*;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HistoryEntryDto {
    private Long id;
    private String eventType;
    private String summary;
    private String details;
    private String performedBy;
    private Instant performedAt;
}
