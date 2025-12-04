package com.ats.assetservice.attachment.service;

import com.ats.assetservice.attachment.dto.AttachmentResponseDto;
import com.ats.assetservice.attachment.entity.AssetAttachment;
import com.ats.assetservice.attachment.entity.AssetHistory;
import com.ats.assetservice.attachment.repository.AssetAttachmentRepository;
import com.ats.assetservice.attachment.repository.AssetHistoryRepository;
import com.ats.assetservice.exception.BadRequestException;
import com.ats.assetservice.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttachmentService {

    private final AssetAttachmentRepository repo;
    private final StorageService storage;
    private final AssetHistoryRepository historyRepo;
    private final LocalStorageService localStorage;

    public AttachmentService(AssetAttachmentRepository repo, StorageService storage,
                             AssetHistoryRepository historyRepo,
                             LocalStorageService localStorage) {
        this.repo = repo;
        this.storage = storage;
        this.historyRepo = historyRepo;
        this.localStorage = localStorage;
    }

    @Transactional
    public AttachmentResponseDto upload(Long assetId, MultipartFile file, String uploadedBy) {
        if (file == null || file.isEmpty()) throw new BadRequestException("File is required");
        if (file.getSize() > 10L * 1024 * 1024) throw new BadRequestException("File too large (max 10MB)");

        try {
            // keyPrefix "asset-{id}" used to organize files under folder per asset
            String keyPrefix = "asset-" + assetId;
            StorageService.StorageResult res = storage.store(file, keyPrefix);

            AssetAttachment entity = AssetAttachment.builder()
                    .assetId(assetId)
                    .fileName(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .s3Key(null) // not used for local storage
                    .url(res.getUrl()) // this stores the download path or relative path
                    .uploadedBy(uploadedBy)
                    .uploadedAt(Instant.now())
                    .build();

            // For local storage we recommend storing a urlPath (relativePath) somewhere in DB
            // We'll assume url currently holds the download-url template; you can add new column url_path as needed

            AssetAttachment saved = repo.save(entity);

            AssetHistory h = AssetHistory.builder()
                    .assetId(assetId)
                    .eventType("ATTACHMENT")
                    .summary("Attachment uploaded: " + saved.getFileName())
                    .details("path=" + res.getKey() + ", url=" + res.getUrl())
                    .performedBy(uploadedBy)
                    .performedAt(Instant.now())
                    .build();
            historyRepo.save(h);

            return AttachmentResponseDto.builder()
                    .id(saved.getId())
                    .assetId(saved.getAssetId())
                    .fileName(saved.getFileName())
                    .contentType(saved.getContentType())
                    .size(saved.getSize())
                    .urlPath(saved.getUrl())
                    .uploadedAt(saved.getUploadedAt())
                    .uploadedBy(saved.getUploadedBy())
                    .build();

        } catch (ResourceNotFoundException rnfe) {
            throw rnfe;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to store file", ex);
        }
    }

    public List<AttachmentResponseDto> listForAsset(Long assetId) {
        return repo.findByAssetIdOrderByUploadedAtDesc(assetId)
                .stream().map(a -> AttachmentResponseDto.builder()
                        .id(a.getId())
                        .assetId(a.getAssetId())
                        .fileName(a.getFileName())
                        .contentType(a.getContentType())
                        .size(a.getSize())
                        .urlPath(a.getUrl())
                        .uploadedAt(a.getUploadedAt())
                        .uploadedBy(a.getUploadedBy())
                        .build())
                .collect(Collectors.toList());
    }

    public AttachmentResponseDto getMetadata(Long assetId, Long attachmentId) {
        AssetAttachment a = repo.findById(attachmentId).filter(x -> x.getAssetId().equals(assetId))
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found"));
        // For local storage, we assume "url" field contains relativePath saved earlier
        return AttachmentResponseDto.builder()
                .id(a.getId())
                .assetId(a.getAssetId())
                .fileName(a.getFileName())
                .contentType(a.getContentType())
                .size(a.getSize())
                .urlPath(a.getUrl())
                .uploadedAt(a.getUploadedAt())
                .uploadedBy(a.getUploadedBy())
                .build();
    }

    @Transactional
    public void delete(Long assetId, Long attachmentId, String deletedBy) {
        AssetAttachment a = repo.findById(attachmentId).filter(x -> x.getAssetId().equals(assetId))
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found"));

        // delete file from local storage using stored path (we used s3Key for key in previous version; adapt accordingly)
        String relativePath = a.getS3Key(); // if you migrated s3Key to local relative path
        if (relativePath == null || relativePath.isEmpty()) {
            // try to derive from url
            relativePath = a.getUrl();
        }
        localStorage.delete(relativePath);

        repo.delete(a);

        AssetHistory h = AssetHistory.builder()
                .assetId(assetId)
                .eventType("ATTACHMENT_DELETE")
                .summary("Attachment deleted: " + a.getFileName())
                .details("path=" + relativePath)
                .performedBy(deletedBy)
                .performedAt(Instant.now())
                .build();
        historyRepo.save(h);
    }
}
