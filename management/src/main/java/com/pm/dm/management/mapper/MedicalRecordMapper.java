package com.pm.dm.management.mapper;

import com.pm.dm.management.dto.medical.MedicalRecordResponseDTO;
import com.pm.dm.management.model.MedicalRecord;

public final class MedicalRecordMapper {
    private MedicalRecordMapper() {
    }

    public static MedicalRecordResponseDTO toDTO(MedicalRecord record) {
        MedicalRecordResponseDTO dto = new MedicalRecordResponseDTO();
        dto.setId(record.getId().toString());
        dto.setVisitDate(record.getVisitDate());
        dto.setDiagnosis(record.getDiagnosis());
        dto.setAllergies(record.getAllergies());
        dto.setNotes(record.getNotes());
        dto.setPatientId(record.getPatient().getId().toString());
        dto.setPatientName(record.getPatient().getFirstName() + " " + record.getPatient().getLastName());
        dto.setDoctorId(record.getDoctor().getId().toString());
        dto.setDoctorName(record.getDoctor().getFirstName() + " " + record.getDoctor().getLastName());
        dto.setCreatedAt(record.getCreatedAt());
        dto.setUpdatedAt(record.getUpdatedAt());
        return dto;
    }
}
