package com.pm.dm.management.mapper;

import com.pm.dm.management.dto.doctor.DoctorRequestDTO;
import com.pm.dm.management.dto.doctor.DoctorResponseDTO;
import com.pm.dm.management.model.Doctor;

public final class DoctorMapper {
    private DoctorMapper() {
    }

    public static DoctorResponseDTO toDTO(Doctor doctor) {
        DoctorResponseDTO dto = new DoctorResponseDTO();
        dto.setId(doctor.getId().toString());
        dto.setFirstName(doctor.getFirstName());
        dto.setLastName(doctor.getLastName());
        dto.setEmail(doctor.getEmail());
        dto.setSpecialization(doctor.getSpecialization());
        dto.setPhoneNumber(doctor.getPhoneNumber());
        dto.setCreatedAt(doctor.getCreatedAt());
        dto.setUpdatedAt(doctor.getUpdatedAt());
        return dto;
    }

    public static Doctor toModel(DoctorRequestDTO requestDTO) {
        Doctor doctor = new Doctor();
        updateModel(doctor, requestDTO);
        return doctor;
    }

    public static void updateModel(Doctor doctor, DoctorRequestDTO requestDTO) {
        doctor.setFirstName(requestDTO.getFirstName());
        doctor.setLastName(requestDTO.getLastName());
        doctor.setEmail(requestDTO.getEmail());
        doctor.setSpecialization(requestDTO.getSpecialization());
        doctor.setPhoneNumber(requestDTO.getPhoneNumber());
    }
}
