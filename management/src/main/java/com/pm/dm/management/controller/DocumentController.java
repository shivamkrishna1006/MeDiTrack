package com.pm.dm.management.controller;

import com.pm.dm.management.dto.common.PageResponse;
import com.pm.dm.management.dto.document.DocumentDownload;
import com.pm.dm.management.dto.document.DocumentResponseDTO;
import com.pm.dm.management.model.DocumentType;
import com.pm.dm.management.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@Tag(name = "Documents", description = "Upload and download patient documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a patient document")
    public ResponseEntity<DocumentResponseDTO> upload(@RequestParam UUID patientId,
                                                      @RequestParam(required = false) UUID medicalRecordId,
                                                      @RequestParam DocumentType documentType,
                                                      @RequestParam MultipartFile file) {
        return ResponseEntity.status(201).body(documentService.upload(patientId, medicalRecordId, documentType, file));
    }

    @GetMapping
    @Operation(summary = "Get documents with pagination and filters")
    public ResponseEntity<PageResponse<DocumentResponseDTO>> getDocuments(@RequestParam(required = false) UUID patientId,
                                                                          @RequestParam(required = false) DocumentType documentType,
                                                                          @RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(documentService.getDocuments(patientId, documentType, page, size));
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Download a document")
    public ResponseEntity<?> download(@PathVariable UUID id) {
        DocumentDownload download = documentService.download(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(download.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(download.getFilename()).build().toString())
                .body(download.getResource());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a document")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        documentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
