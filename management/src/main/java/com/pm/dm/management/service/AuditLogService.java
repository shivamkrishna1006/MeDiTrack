package com.pm.dm.management.service;

import com.pm.dm.management.dto.audit.AuditLogResponseDTO;
import com.pm.dm.management.dto.common.PageResponse;
import com.pm.dm.management.mapper.AuditLogMapper;
import com.pm.dm.management.model.AuditLog;
import com.pm.dm.management.repository.AuditLogRepository;
import com.pm.dm.management.specification.AuditLogSpecification;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(String action, String entityType, UUID entityId, String message) {
        AuditLog auditLog = new AuditLog();
        auditLog.setActor(currentActor());
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setMessage(message);
        auditLogRepository.save(auditLog);
    }

    @Transactional(readOnly = true)
    public PageResponse<AuditLogResponseDTO> getAuditLogs(String actor,
                                                          String action,
                                                          String entityType,
                                                          UUID entityId,
                                                          int page,
                                                          int size) {
        var pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        var result = auditLogRepository.findAll(AuditLogSpecification.search(actor, action, entityType, entityId), pageable)
                .map(AuditLogMapper::toDTO);
        return PageResponse.from(result);
    }

    private String currentActor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "system";
        }
        return authentication.getName();
    }
}
