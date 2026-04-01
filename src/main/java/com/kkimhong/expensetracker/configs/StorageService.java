package com.kkimhong.expensetracker.configs;

import com.kkimhong.expensetracker.dtos.response.PresignedUrlResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class StorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String bucket;

    @PostConstruct
    public void init() {
        log.info("Supabase URL: {}", supabaseUrl);
        log.info("Supabase Bucket: {}", bucket);
        log.info("Supabase Key length: {}", supabaseKey != null ? supabaseKey.length() : "NULL");
    }

    private final WebClient webClient;

    public StorageService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public PresignedUrlResponse generatePresignedUrl(String filename, String contentType) {
        validateFileType(contentType);

        String extension  = getExtension(filename);
        String filePath = "uploads/" + UUID.randomUUID() + "." + extension;

        try {
            Map<String, Object> body = Map.of("expiresIn", 300); // 5 min

            Map<?, ?> response = webClient.post()
                    // FIX 1: Add "/upload" to the URI
                    .uri(supabaseUrl + "/storage/v1/object/upload/sign/" + bucket + "/" + filePath)
                    .header("Authorization", "Bearer " + supabaseKey)
                    // FIX 2: Supabase API Gateway expects the apikey header
                    .header("apikey", supabaseKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            // FIX 3: Check for "url" instead of "signedURL"
            if (response == null || !response.containsKey("url")) {
                throw new RuntimeException("Failed to get upload URL from Supabase. Response: " + response);
            }

            // FIX 4: Build the final URLs correctly
            String returnedPath = (String) response.get("url"); // Looks like: "/object/upload/sign/..."
            String uploadUrl    = supabaseUrl + "/storage/v1" + returnedPath;
            String publicUrl    = supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + filePath;

            log.info("Generated presigned upload URL for: {}", filePath);

            return new PresignedUrlResponse(uploadUrl, publicUrl, filePath);

        } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
            // Pro-tip: Catch WebClient errors specifically to log the exact Supabase response body
            log.error("Supabase API Error! Status: {}, Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to generate upload URL");
        } catch (Exception e) {
            log.error("Failed to generate presigned URL: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate upload URL");
        }
    }

    public void deleteFile(String receiptUrl) {
        if (receiptUrl == null || receiptUrl.isBlank()) return;

        try {
            // Extract file path from public URL
            String prefix = "/storage/v1/object/public/" + bucket + "/";
            String filePath = receiptUrl.substring(receiptUrl.indexOf(prefix) + prefix.length());

            webClient.delete()
                    .uri(supabaseUrl + "/storage/v1/object/" + bucket + "/" + filePath)
                    .header("Authorization", "Bearer " + supabaseKey)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            log.info("Deleted file: {}", filePath);

        } catch (Exception e) {
            log.error("Failed to delete file: {}", e.getMessage());
        }
    }

    private void validateFileType(String contentType) {
        List<String> allowed = List.of(
                "image/jpeg",
                "image/png",
                "image/webp",
                "application/pdf"
        );
        if (!allowed.contains(contentType)) {
            throw new IllegalArgumentException(
                    "Only JPEG, PNG, WEBP and PDF files are allowed"
            );
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "jpg";
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

//    public PresignedUrlResponse generatePresignedUrl(String filename, String contentType) {
//        validateFileType(contentType);
//
//        String extension  = getExtension(filename);
//        String filePath   = "receipts/" + UUID.randomUUID() + "." + extension;
//
//        try {
//            Map<String, Object> body = Map.of("expiresIn", 300); // 5 min
//
//            Map<?, ?> response = webClient.post()
//                    .uri(supabaseUrl + "/storage/v1/object/sign/" + bucket + "/" + filePath)
//                    .header("Authorization", "Bearer " + supabaseKey)
//                    .header("Content-Type", "application/json")
//                    .bodyValue(body)
//                    .retrieve()
//                    .bodyToMono(Map.class)
//                    .block();
//
//            if (response == null || !response.containsKey("signedURL")) {
//                throw new RuntimeException("Failed to get signed URL from Supabase");
//            }
//
//            String signedUrl  = (String) response.get("signedURL");
//            String publicUrl  = supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + filePath;
//            String uploadUrl  = supabaseUrl + signedUrl;
//
//            log.info("Generated presigned URL for: {}", filePath);
//
//            return new PresignedUrlResponse(uploadUrl, publicUrl, filePath);
//
//        } catch (Exception e) {
//            log.error("Failed to generate presigned URL: {}", e.getMessage());
//            throw new RuntimeException("Failed to generate upload URL");
//        }
//    }
}
