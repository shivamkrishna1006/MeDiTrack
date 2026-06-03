package com.pm.dm.management.service;

import com.pm.dm.management.dto.billing.BillingRecordRequestDTO;
import com.pm.dm.management.exception.BadRequestException;
import com.pm.dm.management.model.BillingStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@Transactional
class BillingRecordServiceTests {

    private static final UUID SEEDED_PATIENT_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final UUID OTHER_PATIENT_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
    private static final UUID SEEDED_APPOINTMENT_ID = UUID.fromString("523e4567-e89b-12d3-a456-426614174000");

    @Autowired
    private BillingRecordService billingRecordService;

    @Test
    void shouldRejectBillingWhenAppointmentBelongsToDifferentPatient() {
        BillingRecordRequestDTO request = new BillingRecordRequestDTO();
        request.setPatientId(OTHER_PATIENT_ID);
        request.setAppointmentId(SEEDED_APPOINTMENT_ID);
        request.setInvoiceNumber("INV-1001");
        request.setAmount(new BigDecimal("1500.00"));
        request.setStatus(BillingStatus.PENDING);
        request.setDueDate(LocalDate.now().plusDays(10));
        request.setDescription("Consultation fee");

        assertThatThrownBy(() -> billingRecordService.createBillingRecord(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Appointment patient does not match");
    }
}
