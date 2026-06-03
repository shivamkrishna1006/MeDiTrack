package com.pm.dm.management.service;

import com.pm.dm.management.dto.common.PageResponse;
import com.pm.dm.management.dto.document.DocumentDownload;
import com.pm.dm.management.dto.document.DocumentResponseDTO;
import com.pm.dm.management.exception.BadRequestException;
import com.pm.dm.management.exception.ResourceNotFoundException;
import com.pm.dm.management.mapper.DocumentMapper;
import com.pm.dm.management.model.Document;
import com.pm.dm.management.model.DocumentType;
import com.pm.dm.management.model.MedicalRecord;
import com.pm.dm.management.repository.DocumentRepository;
import com.pm.dm.management.specification.DocumentSpecification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Transactional
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final PatientService patientService;
    private final MedicalRecordService medicalRecordService;
    private final AuditLogService auditLogService;
    private final Path storageDir;

    public DocumentService(DocumentRepository documentRepository,
                           PatientService patientService,
                           MedicalRecordService medicalRecordService,
                           AuditLogService auditLogService,
                           @Value("${app.documents.storage-dir}") String storageDir) {
        this.documentRepository = documentRepository;
        this.patientService = patientService;
        this.medicalRecordService = medicalRecordService;
        this.auditLogService = auditLogService;
        this.storageDir = Path.of(storageDir).toAbsolutePath().normalize();
    }

    public DocumentResponseDTO upload(UUID patientId, UUID medicalRecordId, DocumentType type, MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("Document file is required");
        }
        var patient = patientService.findPatient(patientId);
        MedicalRecord medicalRecord = null;
        if (medicalRecordId != null) {
            medicalRecord = medicalRecordService.findMedicalRecord(medicalRecordId);
            if (!medicalRecord.getPatient().getId().equals(patient.getId())) {
                throw new BadRequestException("Medical record patient does not match document patient");
            }
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "document");
        String storedFilename = UUID.randomUUID() + "-" + originalFilename;
        Path destination = storageDir.resolve(storedFilename).normalize();

        try {
            Files.createDirectories(storageDir);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new BadRequestException("Could not store document: " + ex.getMessage());
        }

        Document document = new Document();
        document.setPatient(patient);
        document.setMedicalRecord(medicalRecord);
        document.setDocumentType(type);
        document.setOriginalFilename(originalFilename);
        document.setStoredFilename(storedFilename);
        document.setContentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream");
        document.setSizeBytes(file.getSize());
        Document saved = documentRepository.save(document);
        auditLogService.log("DOCUMENT_UPLOADED", "Document", saved.getId(), originalFilename);
        return DocumentMapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<DocumentResponseDTO> getDocuments(UUID patientId, DocumentType type, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        var result = documentRepository.findAll(DocumentSpecification.search(patientId, type), pageable)
                .map(DocumentMapper::toDTO);
        return PageResponse.from(result);
    }

    @Transactional(readOnly = true)
    public DocumentDownload download(UUID id) {
        Document document = findDocument(id);
        Path path = storageDir.resolve(document.getStoredFilename()).normalize();
        Resource resource = new FileSystemResource(path);
        if (!resource.exists()) {
            throw new ResourceNotFoundException("Stored file not found for document id: " + id);
        }
        DocumentDownload download = new DocumentDownload();
        download.setResource(resource);
        download.setFilename(document.getOriginalFilename());
        download.setContentType(document.getContentType());
        return download;
    }

    public void delete(UUID id) {
        Document document = findDocument(id);
        document.markDeleted();
        documentRepository.save(document);
        auditLogService.log("DOCUMENT_DELETED", "Document", document.getId(), document.getOriginalFilename());
    }

    @Transactional(readOnly = true)
    public long countDocuments() {
        return documentRepository.count();
    }

    private Document findDocument(UUID id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + id));
    }
}
