package com.ats.assetservice.attachment.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    // store returns an object with S3 key and public URL
    StorageResult store(MultipartFile file, String keyPrefix) throws Exception;

    class StorageResult {
        private final String key;
        private final String url;

        public StorageResult(String key, String url) {
            this.key = key;
            this.url = url;
        }

        public String getKey() { return key; }
        public String getUrl() { return url; }
    }
}
