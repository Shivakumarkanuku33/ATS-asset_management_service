package com.ats.assetservice.attachment.controller;


import com.ats.assetservice.attachment.dto.AttachmentResponseDto;
import com.ats.assetservice.attachment.dto.HistoryEntryDto;
import com.ats.assetservice.attachment.service.AttachmentService;
import com.ats.assetservice.attachment.service.HistoryService;
import com.ats.assetservice.attachment.service.LocalStorageService;

import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
//@RequestMapping("/assets")
public class AssetAttachmentController {

    private final AttachmentService attachmentService;
    private final HistoryService historyService;
    private final LocalStorageService localStorageService;

    public AssetAttachmentController(
            AttachmentService attachmentService,
            HistoryService historyService,
            LocalStorageService localStorageService) {
        this.attachmentService = attachmentService;
        this.historyService = historyService;
        this.localStorageService = localStorageService;
    }

    // Upload - only ADMIN and ASSET_MANAGER can upload attachment
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER')")
    @PostMapping(value = "/{assetId}/upload-attachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@PathVariable Long assetId,
                                    @RequestPart("file") MultipartFile file,
                                    @RequestParam(value = "uploadedBy", required = false) String uploadedBy) {
        if (uploadedBy == null) uploadedBy = "system";
        AttachmentResponseDto dto = attachmentService.upload(assetId, file, uploadedBy);
        return ResponseEntity.status(201).body(dto);
    }

    // List attachments - authenticated users only
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{assetId}/attachments")
    public ResponseEntity<List<AttachmentResponseDto>> listAttachments(@PathVariable Long assetId) {
        return ResponseEntity.ok(attachmentService.listForAsset(assetId));
    }

    // Download attachment - authenticated users only
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{assetId}/attachments/{attachmentId}/download")
    public ResponseEntity<Resource> download(@PathVariable Long assetId,
                                             @PathVariable Long attachmentId) {
        AttachmentResponseDto meta = attachmentService.getMetadata(assetId, attachmentId);
        Resource resource = localStorageService.loadAsResource(meta.getUrlPath());

        String filename = meta.getFileName();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename).build().toString())
                .contentType(MediaType.parseMediaType(
                        meta.getContentType() == null ? "application/octet-stream" : meta.getContentType()))
                .body(resource);
    }

    // Delete attachment - only ADMIN and ASSET_MANAGER
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER')")
    @DeleteMapping("/{assetId}/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(
            @PathVariable Long assetId,
            @PathVariable Long attachmentId,
            @RequestParam(value = "deletedBy", required = false) String deletedBy) {

        if (deletedBy == null) deletedBy = "system";
        attachmentService.delete(assetId, attachmentId, deletedBy);
        return ResponseEntity.noContent().build();
    }

    // Asset History - authenticated users only
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{assetId}/history")
    public ResponseEntity<List<HistoryEntryDto>> getHistory(@PathVariable Long assetId) {
        return ResponseEntity.ok(historyService.getHistoryForAsset(assetId));
    }
}

