package com.pm.dm.management.mapper;

import com.pm.dm.management.dto.prescription.PrescriptionResponseDTO;
import com.pm.dm.management.model.Prescription;

public final class PrescriptionMapper {
    private PrescriptionMapper() {
    }

    public static PrescriptionResponseDTO toDTO(Prescription prescription) {
        PrescriptionResponseDTO dto = new PrescriptionResponseDTO();
        dto.setId(prescription.getId().toString());
        dto.setMedicationName(prescription.getMedicationName());
        dto.setDosage(prescription.getDosage());
        dto.setFrequency(prescription.getFrequency());
        dto.setDurationInDays(prescription.getDurationInDays());
        dto.setInstructions(prescription.getInstructions());
        dto.setStatus(prescription.getStatus());
        dto.setPatientId(prescription.getPatient().getId().toString());
        dto.setPatientName(prescription.getPatient().getFirstName() + " " + prescription.getPatient().getLastName());
        dto.setDoctorId(prescription.getDoctor().getId().toString());
        dto.setDoctorName(prescription.getDoctor().getFirstName() + " " + prescription.getDoctor().getLastName());
        dto.setMedicalRecordId(prescription.getMedicalRecord() != null ? prescription.getMedicalRecord().getId().toString() : null);
        dto.setCreatedAt(prescription.getCreatedAt());
        dto.setUpdatedAt(prescription.getUpdatedAt());
        return dto;
    }
}
