package com.pm.dm.management.controller;

import com.pm.dm.management.dto.common.PageResponse;
import com.pm.dm.management.dto.notification.NotificationRequestDTO;
import com.pm.dm.management.dto.notification.NotificationResponseDTO;
import com.pm.dm.management.model.NotificationStatus;
import com.pm.dm.management.model.NotificationType;
import com.pm.dm.management.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications", description = "Queue and inspect notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    @Operation(summary = "Queue a notification")
    public ResponseEntity<NotificationResponseDTO> queue(@Valid @RequestBody NotificationRequestDTO request) {
        return ResponseEntity.status(201).body(notificationService.queue(request));
    }

    @PostMapping("/send-pending")
    @Operation(summary = "Send pending notifications immediately")
    public ResponseEntity<Void> sendPending() {
        notificationService.sendPendingNow();
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    @Operation(summary = "Get notifications with pagination and filters")
    public ResponseEntity<PageResponse<NotificationResponseDTO>> getNotifications(
            @RequestParam(required = false) NotificationStatus status,
            @RequestParam(required = false) NotificationType type,
            @RequestParam(required = false) String recipientEmail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(notificationService.getNotifications(status, type, recipientEmail, page, size));
    }
}
