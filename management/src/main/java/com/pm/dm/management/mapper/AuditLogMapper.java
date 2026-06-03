package com.pm.dm.management.mapper;

import com.pm.dm.management.dto.audit.AuditLogResponseDTO;
import com.pm.dm.management.model.AuditLog;

public final class AuditLogMapper {
    private AuditLogMapper() {
    }

    public static AuditLogResponseDTO toDTO(AuditLog auditLog) {
        AuditLogResponseDTO dto = new AuditLogResponseDTO();
        dto.setId(auditLog.getId().toString());
        dto.setActor(auditLog.getActor());
        dto.setAction(auditLog.getAction());
        dto.setEntityType(auditLog.getEntityType());
        dto.setEntityId(auditLog.getEntityId() != null ? auditLog.getEntityId().toString() : null);
        dto.setMessage(auditLog.getMessage());
        dto.setCreatedAt(auditLog.getCreatedAt());
        return dto;
    }
}
