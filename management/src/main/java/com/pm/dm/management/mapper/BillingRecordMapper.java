package com.pm.dm.management.mapper;

import com.pm.dm.management.dto.billing.BillingRecordResponseDTO;
import com.pm.dm.management.model.BillingRecord;

public final class BillingRecordMapper {
    private BillingRecordMapper() {
    }

    public static BillingRecordResponseDTO toDTO(BillingRecord billingRecord) {
        BillingRecordResponseDTO dto = new BillingRecordResponseDTO();
        dto.setId(billingRecord.getId().toString());
        dto.setInvoiceNumber(billingRecord.getInvoiceNumber());
        dto.setAmount(billingRecord.getAmount());
        dto.setStatus(billingRecord.getStatus());
        dto.setDueDate(billingRecord.getDueDate());
        dto.setPaidDate(billingRecord.getPaidDate());
        dto.setDescription(billingRecord.getDescription());
        dto.setPatientId(billingRecord.getPatient().getId().toString());
        dto.setPatientName(billingRecord.getPatient().getFirstName() + " " + billingRecord.getPatient().getLastName());
        dto.setAppointmentId(billingRecord.getAppointment() != null ? billingRecord.getAppointment().getId().toString() : null);
        dto.setCreatedAt(billingRecord.getCreatedAt());
        dto.setUpdatedAt(billingRecord.getUpdatedAt());
        return dto;
    }
}
