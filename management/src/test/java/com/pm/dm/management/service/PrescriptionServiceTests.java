package com.pm.dm.management.service;

import com.pm.dm.management.dto.medical.MedicalRecordRequestDTO;
import com.pm.dm.management.dto.prescription.PrescriptionRequestDTO;
import com.pm.dm.management.exception.BadRequestException;
import com.pm.dm.management.model.PrescriptionStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@Transactional
class PrescriptionServiceTests {

    private static final UUID SEEDED_PATIENT_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final UUID OTHER_PATIENT_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
    private static final UUID SEEDED_DOCTOR_ID = UUID.fromString("423e4567-e89b-12d3-a456-426614174000");

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private MedicalRecordService medicalRecordService;

    @Test
    void shouldRejectPrescriptionWhenMedicalRecordBelongsToDifferentPatient() {
        MedicalRecordRequestDTO medicalRecordRequest = new MedicalRecordRequestDTO();
        medicalRecordRequest.setPatientId(OTHER_PATIENT_ID);
        medicalRecordRequest.setDoctorId(SEEDED_DOCTOR_ID);
        medicalRecordRequest.setVisitDate(LocalDate.now());
        medicalRecordRequest.setDiagnosis("Flu");
        var record = medicalRecordService.createMedicalRecord(medicalRecordRequest);

        PrescriptionRequestDTO request = new PrescriptionRequestDTO();
        request.setPatientId(SEEDED_PATIENT_ID);
        request.setDoctorId(SEEDED_DOCTOR_ID);
        request.setMedicalRecordId(UUID.fromString(record.getId()));
        request.setMedicationName("Medicine A");
        request.setDosage("1 tablet");
        request.setFrequency("Twice daily");
        request.setDurationInDays(5);
        request.setStatus(PrescriptionStatus.ACTIVE);

        assertThatThrownBy(() -> prescriptionService.createPrescription(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Medical record patient does not match");
    }
}
