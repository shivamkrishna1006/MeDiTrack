package com.pm.dm.management.controller;

import com.pm.dm.management.dto.audit.AuditLogResponseDTO;
import com.pm.dm.management.dto.common.PageResponse;
import com.pm.dm.management.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit-logs")
@Tag(name = "Audit Logs", description = "Inspect application audit events")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    @Operation(summary = "Get audit logs with pagination and filters")
    public ResponseEntity<PageResponse<AuditLogResponseDTO>> getAuditLogs(@RequestParam(required = false) String actor,
                                                                          @RequestParam(required = false) String action,
                                                                          @RequestParam(required = false) String entityType,
                                                                          @RequestParam(required = false) UUID entityId,
                                                                          @RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(auditLogService.getAuditLogs(actor, action, entityType, entityId, page, size));
    }
}
