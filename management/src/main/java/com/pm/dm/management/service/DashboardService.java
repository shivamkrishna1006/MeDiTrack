package com.pm.dm.management.service;

import com.pm.dm.management.dto.dashboard.DashboardSummaryResponse;
import com.pm.dm.management.model.BillingStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class DashboardService {

    private final PatientService patientService;
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final MedicalRecordService medicalRecordService;
    private final PrescriptionService prescriptionService;
    private final BillingRecordService billingRecordService;
    private final DocumentService documentService;

    public DashboardService(PatientService patientService,
                            DoctorService doctorService,
                            AppointmentService appointmentService,
                            MedicalRecordService medicalRecordService,
                            PrescriptionService prescriptionService,
                            BillingRecordService billingRecordService,
                            DocumentService documentService) {
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
        this.medicalRecordService = medicalRecordService;
        this.prescriptionService = prescriptionService;
        this.billingRecordService = billingRecordService;
        this.documentService = documentService;
    }

    public DashboardSummaryResponse getSummary() {
        DashboardSummaryResponse response = new DashboardSummaryResponse();
        response.setTotals(Map.of(
                "patients", patientService.countPatients(),
                "doctors", doctorService.countDoctors(),
                "appointments", appointmentService.countAppointments(),
                "medicalRecords", medicalRecordService.countMedicalRecords(),
                "prescriptions", prescriptionService.countPrescriptions(),
                "billingRecords", billingRecordService.countBillingRecords(),
                "documents", documentService.countDocuments()
        ));
        response.setAppointmentsByStatus(appointmentService.countAppointmentsByStatus());
        response.setBillingByStatus(billingRecordService.countBillingByStatus());
        response.setNewPatientsThisMonth(patientService.countNewPatientsThisMonth());
        response.setUpcomingAppointments(appointmentService.countUpcomingAppointments());
        response.setOverdueBills(billingRecordService.countOverdueBills());
        response.setPaidRevenue(valueOrZero(billingRecordService.sumAmountByStatus(BillingStatus.PAID)));
        response.setPendingRevenue(valueOrZero(billingRecordService.sumAmountByStatus(BillingStatus.PENDING)));
        return response;
    }

    private BigDecimal valueOrZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
