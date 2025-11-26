package com.ats.assetservice.attachment.dto;

import lombok.*;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttachmentResponseDto {
    private Long id;
    private Long assetId;
    private String fileName;
    private String contentType;
    private Long size;
    private String urlPath;
    private Instant uploadedAt;
    private String uploadedBy;
}
