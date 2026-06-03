package com.pm.dm.management.service;

import com.pm.dm.management.dto.billing.BillingRecordRequestDTO;
import com.pm.dm.management.dto.billing.BillingRecordResponseDTO;
import com.pm.dm.management.dto.common.PageResponse;
import com.pm.dm.management.exception.BadRequestException;
import com.pm.dm.management.exception.ConflictException;
import com.pm.dm.management.exception.ResourceNotFoundException;
import com.pm.dm.management.mapper.BillingRecordMapper;
import com.pm.dm.management.model.BillingRecord;
import com.pm.dm.management.model.BillingStatus;
import com.pm.dm.management.repository.BillingRecordRepository;
import com.pm.dm.management.specification.BillingRecordSpecification;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@Transactional
public class BillingRecordService {

    private final BillingRecordRepository billingRecordRepository;
    private final PatientService patientService;
    private final AppointmentService appointmentService;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    public BillingRecordService(BillingRecordRepository billingRecordRepository,
                                PatientService patientService,
                                AppointmentService appointmentService,
                                AuditLogService auditLogService,
                                NotificationService notificationService) {
        this.billingRecordRepository = billingRecordRepository;
        this.patientService = patientService;
        this.appointmentService = appointmentService;
        this.auditLogService = auditLogService;
        this.notificationService = notificationService;
    }

    @Transactional(readOnly = true)
    public PageResponse<BillingRecordResponseDTO> getBillingRecords(UUID patientId,
                                                                    BillingStatus status,
                                                                    int page,
                                                                    int size,
                                                                    String sortBy,
                                                                    String direction) {
        var pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);
        var pageResult = billingRecordRepository.findAll(BillingRecordSpecification.search(patientId, status), pageable)
                .map(BillingRecordMapper::toDTO);
        return PageResponse.from(pageResult);
    }

    @Transactional(readOnly = true)
    public BillingRecordResponseDTO getBillingRecord(UUID id) {
        return BillingRecordMapper.toDTO(findBillingRecord(id));
    }

    public BillingRecordResponseDTO createBillingRecord(BillingRecordRequestDTO request) {
        if (billingRecordRepository.existsByInvoiceNumber(request.getInvoiceNumber())) {
            throw new ConflictException("Invoice number already exists: " + request.getInvoiceNumber());
        }
        validateBilling(request);
        BillingRecord billingRecord = new BillingRecord();
        apply(billingRecord, request);
        BillingRecord saved = billingRecordRepository.save(billingRecord);
        auditLogService.log("BILL_CREATED", "BillingRecord", saved.getId(), saved.getInvoiceNumber());
        if (saved.getStatus() == BillingStatus.PENDING) {
            notificationService.queueSystemEmail(com.pm.dm.management.model.NotificationType.BILLING_DUE,
                    saved.getPatient().getEmail(),
                    "Billing due",
                    "Invoice " + saved.getInvoiceNumber() + " is due on " + saved.getDueDate(),
                    saved.getId(),
                    "BillingRecord");
        }
        return BillingRecordMapper.toDTO(saved);
    }

    public BillingRecordResponseDTO updateBillingRecord(UUID id, BillingRecordRequestDTO request) {
        BillingRecord billingRecord = findBillingRecord(id);
        if (billingRecordRepository.existsByInvoiceNumberAndIdNot(request.getInvoiceNumber(), id)) {
            throw new ConflictException("Invoice number already exists: " + request.getInvoiceNumber());
        }
        validateBilling(request);
        apply(billingRecord, request);
        BillingRecord updated = billingRecordRepository.save(billingRecord);
        auditLogService.log(updated.getStatus() == BillingStatus.PAID ? "BILL_PAID" : "BILL_UPDATED",
                "BillingRecord",
                updated.getId(),
                updated.getInvoiceNumber());
        return BillingRecordMapper.toDTO(updated);
    }

    public void deleteBillingRecord(UUID id) {
        BillingRecord billingRecord = findBillingRecord(id);
        billingRecord.markDeleted();
        billingRecordRepository.save(billingRecord);
        auditLogService.log("BILL_DELETED", "BillingRecord", id, billingRecord.getInvoiceNumber());
    }

        @Transactional(readOnly = true)
    public long countBillingRecords() {
        return billingRecordRepository.count();
    }

    @Transactional(readOnly = true)
    public long countOverdueBills() {
        return billingRecordRepository.countByStatusAndDueDateBefore(BillingStatus.PENDING, java.time.LocalDate.now());
    }

    @Transactional(readOnly = true)
    public java.math.BigDecimal sumAmountByStatus(BillingStatus status) {
        return billingRecordRepository.sumAmountByStatus(status);
    }

    @Transactional(readOnly = true)
    public java.util.Map<String, Long> countBillingByStatus() {
        return billingRecordRepository.countByStatus()
                .stream()
                .collect(java.util.stream.Collectors.toMap(row -> row.getStatus().name(), com.pm.dm.management.repository.BillingRecordRepository.StatusCount::getCount));
    }

    private BillingRecord findBillingRecord(UUID id) {
        return billingRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Billing record not found with id: " + id));
    }

    private void validateBilling(BillingRecordRequestDTO request) {
        if (request.getStatus() == BillingStatus.PAID && request.getPaidDate() == null) {
            throw new BadRequestException("Paid date is required when billing status is PAID");
        }
        if (request.getPaidDate() != null && request.getStatus() != BillingStatus.PAID) {
            throw new BadRequestException("Paid date can only be set when billing status is PAID");
        }
        if (request.getPaidDate() != null && request.getPaidDate().isBefore(request.getDueDate())) {
            throw new BadRequestException("Paid date cannot be before due date");
        }
        if (request.getDueDate().isBefore(LocalDate.now().minusYears(1))) {
            throw new BadRequestException("Due date is too far in the past");
        }
    }

    private void apply(BillingRecord billingRecord, BillingRecordRequestDTO request) {
        var patient = patientService.findPatient(request.getPatientId());
        billingRecord.setPatient(patient);
        billingRecord.setInvoiceNumber(request.getInvoiceNumber());
        billingRecord.setAmount(request.getAmount());
        billingRecord.setStatus(request.getStatus());
        billingRecord.setDueDate(request.getDueDate());
        billingRecord.setPaidDate(request.getPaidDate());
        billingRecord.setDescription(request.getDescription());
        if (request.getAppointmentId() != null) {
            var appointment = appointmentService.findAppointmentEntity(request.getAppointmentId());
            if (!appointment.getPatient().getId().equals(patient.getId())) {
                throw new BadRequestException("Appointment patient does not match billing patient");
            }
            billingRecord.setAppointment(appointment);
        } else {
            billingRecord.setAppointment(null);
        }
    }
}
