package com.ats.assetservice.attachment.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.UUID;

@Service
public class LocalStorageService implements StorageService {

    private final Path rootLocation;

    public LocalStorageService(@Value("${file.storage.upload-dir:uploads}") String uploadDir) {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage directory", e);
        }
    }

    @Override
    public StorageResult store(MultipartFile file, String keyPrefix) throws Exception {
        if (file.isEmpty()) throw new IOException("Failed to store empty file.");

        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.'));
        }
        String filename = String.format("%s_%d_%s%s", keyPrefix.replaceAll("[^a-zA-Z0-9_-]", ""), Instant.now().toEpochMilli(), UUID.randomUUID(), ext);

        Path targetDirectory = this.rootLocation.resolve(keyPrefix).normalize();
        Files.createDirectories(targetDirectory);

        Path targetLocation = targetDirectory.resolve(filename).normalize();
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        String relativePath = this.rootLocation.relativize(targetLocation).toString().replace('\\', '/');
        String url = "/assets/" + keyPrefix + "/files/" + filename; // this is the download path template -  controller maps it

        return new StorageResult(relativePath, url);
    }

    // Additional helpers for controller
    public Resource loadAsResource(String relativePath) {
        try {
            Path file = this.rootLocation.resolve(relativePath).normalize();
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + relativePath);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not read file: " + relativePath, e);
        }
    }

    public void delete(String relativePath) {
        try {
            Path file = this.rootLocation.resolve(relativePath).normalize();
            FileSystemUtils.deleteRecursively(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + relativePath, e);
        }
    }
}
