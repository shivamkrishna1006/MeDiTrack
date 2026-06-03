package com.pm.dm.management.mapper;

import com.pm.dm.management.dto.appointment.AppointmentResponseDTO;
import com.pm.dm.management.model.Appointment;

public final class AppointmentMapper {
    private AppointmentMapper() {
    }

    public static AppointmentResponseDTO toDTO(Appointment appointment) {
        AppointmentResponseDTO dto = new AppointmentResponseDTO();
        dto.setId(appointment.getId().toString());
        dto.setAppointmentTime(appointment.getAppointmentTime());
        dto.setEndTime(appointment.getEndTime());
        dto.setDurationMinutes(appointment.getDurationMinutes());
        dto.setStatus(appointment.getStatus());
        dto.setNotes(appointment.getNotes());
        dto.setPatientId(appointment.getPatient().getId().toString());
        dto.setPatientName(appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName());
        dto.setDoctorId(appointment.getDoctor().getId().toString());
        dto.setDoctorName(appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName());
        dto.setCreatedAt(appointment.getCreatedAt());
        dto.setUpdatedAt(appointment.getUpdatedAt());
        return dto;
    }
}
