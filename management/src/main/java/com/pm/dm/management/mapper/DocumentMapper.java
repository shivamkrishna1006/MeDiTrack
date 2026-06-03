package com.pm.dm.management.mapper;

import com.pm.dm.management.dto.document.DocumentResponseDTO;
import com.pm.dm.management.model.Document;

public final class DocumentMapper {
    private DocumentMapper() {
    }

    public static DocumentResponseDTO toDTO(Document document) {
        DocumentResponseDTO dto = new DocumentResponseDTO();
        dto.setId(document.getId().toString());
        dto.setDocumentType(document.getDocumentType());
        dto.setOriginalFilename(document.getOriginalFilename());
        dto.setContentType(document.getContentType());
        dto.setSizeBytes(document.getSizeBytes());
        dto.setPatientId(document.getPatient().getId().toString());
        dto.setPatientName(document.getPatient().getFirstName() + " " + document.getPatient().getLastName());
        dto.setMedicalRecordId(document.getMedicalRecord() != null ? document.getMedicalRecord().getId().toString() : null);
        dto.setCreatedAt(document.getCreatedAt());
        return dto;
    }
}
