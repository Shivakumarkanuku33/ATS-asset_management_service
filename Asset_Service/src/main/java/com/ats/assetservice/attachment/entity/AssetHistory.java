package com.ats.assetservice.attachment.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "asset_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "asset_id", nullable = false)
    private Long assetId;

    // type could be ASSIGNMENT, STATUS_CHANGE, MAINTENANCE, ATTACHMENT, NOTE etc.
    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "summary", length = 2000)
    private String summary;

    @Column(name = "details", length = 8000)
    private String details;

    @Column(name = "performed_by")
    private String performedBy;

    @Column(name = "performed_at")
    private Instant performedAt;
}
