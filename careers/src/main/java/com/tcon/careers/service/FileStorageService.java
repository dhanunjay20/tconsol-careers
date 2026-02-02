package com.tcon.careers.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    @Value("${gcp.storage.bucket-name}")
    private String bucketName;

    @Value("${gcp.storage.project-id}")
    private String projectId;

    @Value("${gcp.storage.credentials-path}")
    private String credentialsPath;

    private Storage storage;
    private boolean gcpInitialized = false;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("pdf", "doc", "docx");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @PostConstruct
    public void initializeStorage() {
        try {
            // Try multiple paths to locate the credentials file
            File credentialsFile = findCredentialsFile();

            if (credentialsFile != null && credentialsFile.exists()) {
                GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsFile));
                this.storage = StorageOptions.newBuilder()
                        .setProjectId(projectId)
                        .setCredentials(credentials)
                        .build()
                        .getService();
                gcpInitialized = true;
                log.info("GCP Storage initialized successfully for project: {}", projectId);
            } else {
                log.warn("GCP credentials file not found at configured path: {}. GCP Storage will be disabled.", credentialsPath);
                log.warn("To enable GCP Storage, place the credentials file at: {}", credentialsPath);
                gcpInitialized = false;
            }
        } catch (IOException ex) {
            log.warn("Could not initialize GCP Storage: {}. GCP Storage will be disabled. Error: {}", credentialsPath, ex.getMessage());
            log.debug("Full error details:", ex);
            gcpInitialized = false;
        }
    }

    private File findCredentialsFile() {
        // Try 1: Direct path
        File file = new File(credentialsPath);
        if (file.exists()) {
            return file;
        }

        // Try 2: Relative to current working directory
        file = new File(System.getProperty("user.dir"), credentialsPath);
        if (file.exists()) {
            return file;
        }

        // Try 3: Look in careers directory
        file = new File(System.getProperty("user.dir"), "careers/" + credentialsPath);
        if (file.exists()) {
            return file;
        }

        // Try 4: Look in project root
        file = new File(System.getProperty("user.dir"), "../careers/" + credentialsPath);
        if (file.exists()) {
            return file;
        }

        return null;
    }

    public String uploadFile(MultipartFile file, String jobId) {
        if (!gcpInitialized) {
            log.error("GCP Storage is not initialized. Cannot upload file.");
            throw new RuntimeException("GCP Storage is not configured. Please ensure credentials are properly set up.");
        }

        validateFile(file);

        String fileName = generateFileName(file.getOriginalFilename(), jobId);

        try {
            BlobId blobId = BlobId.of(bucketName, fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();

            storage.create(blobInfo, file.getBytes());

            log.info("File uploaded successfully to GCP bucket: {}", fileName);
            return fileName;

        } catch (IOException e) {
            log.error("Error uploading file to GCP: {}", e.getMessage());
            throw new RuntimeException("Failed to upload file to GCP: " + e.getMessage());
        }
    }

    public String getFileUrl(String fileName) {
        // Return the public URL or signed URL for the file in GCP bucket
        // For public buckets: https://storage.googleapis.com/BUCKET_NAME/FILE_NAME
        // For private buckets, you might want to generate a signed URL
        return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
    }

    public void deleteFile(String fileName) {
        if (!gcpInitialized) {
            log.warn("GCP Storage is not initialized. Cannot delete file: {}", fileName);
            return;
        }

        try {
            BlobId blobId = BlobId.of(bucketName, fileName);
            boolean deleted = storage.delete(blobId);
            if (deleted) {
                log.info("File deleted successfully from GCP: {}", fileName);
            } else {
                log.warn("File not found in GCP bucket: {}", fileName);
            }
        } catch (Exception e) {
            log.error("Error deleting file from GCP: {}", e.getMessage());
            throw new RuntimeException("Failed to delete file from GCP: " + e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum limit of 5MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !hasAllowedExtension(originalFilename)) {
            throw new IllegalArgumentException("Invalid file type. Only PDF, DOC, and DOCX files are allowed");
        }
    }

    private boolean hasAllowedExtension(String filename) {
        String extension = getFileExtension(filename);
        return ALLOWED_EXTENSIONS.contains(extension.toLowerCase());
    }

    private String getFileExtension(String filename) {
        int lastIndexOf = filename.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return filename.substring(lastIndexOf + 1);
    }

    private String generateFileName(String originalFilename, String jobId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String extension = getFileExtension(originalFilename);
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return String.format("resumes/%s/resume_%s_%s.%s", jobId, timestamp, uuid, extension);
    }
}
