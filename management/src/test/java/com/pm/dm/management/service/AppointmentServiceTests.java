package com.pm.dm.management.service;

import com.pm.dm.management.dto.appointment.AppointmentRequestDTO;
import com.pm.dm.management.exception.BadRequestException;
import com.pm.dm.management.model.AppointmentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@Transactional
class AppointmentServiceTests {

    private static final UUID SEEDED_PATIENT_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final UUID SEEDED_DOCTOR_ID = UUID.fromString("423e4567-e89b-12d3-a456-426614174000");

    @Autowired
    private AppointmentService appointmentService;

    @Test
    void shouldRejectOverlappingAppointmentsForDoctor() {
        AppointmentRequestDTO first = new AppointmentRequestDTO();
        first.setPatientId(SEEDED_PATIENT_ID);
        first.setDoctorId(SEEDED_DOCTOR_ID);
        first.setAppointmentTime(LocalDateTime.of(2030, 1, 10, 9, 0));
        first.setDurationMinutes(30);
        first.setStatus(AppointmentStatus.SCHEDULED);
        first.setNotes("First visit");
        appointmentService.createAppointment(first);

        AppointmentRequestDTO overlap = new AppointmentRequestDTO();
        overlap.setPatientId(SEEDED_PATIENT_ID);
        overlap.setDoctorId(SEEDED_DOCTOR_ID);
        overlap.setAppointmentTime(LocalDateTime.of(2030, 1, 10, 9, 15));
        overlap.setDurationMinutes(30);
        overlap.setStatus(AppointmentStatus.SCHEDULED);
        overlap.setNotes("Overlap visit");

        assertThatThrownBy(() -> appointmentService.createAppointment(overlap))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Doctor already has an appointment");
    }
}
