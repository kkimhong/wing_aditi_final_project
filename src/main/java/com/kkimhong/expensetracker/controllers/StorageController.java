package com.kkimhong.expensetracker.controllers;

import com.kkimhong.expensetracker.configs.StorageService;
import com.kkimhong.expensetracker.dtos.request.PresignedUrlRequest;
import com.kkimhong.expensetracker.dtos.response.PresignedUrlResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(StorageController.BASE_URL)
@RequiredArgsConstructor
@Validated
public class StorageController {

    public static final String BASE_URL ="/api/v1/storage";
    private final StorageService storageService;

    // Get presigned URL — client uploads directly to Supabase
    @PostMapping("/presigned-url")
    @PreAuthorize("hasAuthority('expenses:create')")
    public ResponseEntity<PresignedUrlResponse> getPresignedUrl(
            @RequestBody @Valid PresignedUrlRequest request) {
        return ResponseEntity.ok(
                storageService.generatePresignedUrl(
                        request.filename(),
                        request.contentType()
                )
        );
    }

    // Delete file — called when expense is deleted or receipt removed
    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('expenses:create')")
    public ResponseEntity<Void> deleteFile(
            @RequestParam String receiptUrl) {
        storageService.deleteFile(receiptUrl);
        return ResponseEntity.noContent().build();
    }
}
