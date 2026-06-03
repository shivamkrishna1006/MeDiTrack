package com.pm.dm.management.mapper;

import com.pm.dm.management.dto.PatientRequestDTO;
import com.pm.dm.management.dto.PatientResponseDTO;
import com.pm.dm.management.model.Patient;

public final class PatientMapper {
    private PatientMapper() {
    }

    public static PatientResponseDTO toDTO(Patient patient) {
        PatientResponseDTO dto = new PatientResponseDTO();
        dto.setId(patient.getId().toString());
        dto.setFirstName(patient.getFirstName());
        dto.setLastName(patient.getLastName());
        dto.setAddress(patient.getAddress());
        dto.setEmail(patient.getEmail());
        dto.setDateOfBirth(patient.getDateOfBirth());
        dto.setRegistrationDate(patient.getRegistrationDate());
        dto.setCreatedAt(patient.getCreatedAt());
        dto.setUpdatedAt(patient.getUpdatedAt());
        return dto;
    }

    public static Patient toModel(PatientRequestDTO requestDTO) {
        Patient patient = new Patient();
        updateModel(patient, requestDTO);
        return patient;
    }

    public static void updateModel(Patient patient, PatientRequestDTO requestDTO) {
        patient.setFirstName(requestDTO.getFirstName());
        patient.setLastName(requestDTO.getLastName());
        patient.setAddress(requestDTO.getAddress());
        patient.setEmail(requestDTO.getEmail());
        patient.setDateOfBirth(requestDTO.getDateOfBirth());
        patient.setRegistrationDate(requestDTO.getRegisteredDate());
    }
}
