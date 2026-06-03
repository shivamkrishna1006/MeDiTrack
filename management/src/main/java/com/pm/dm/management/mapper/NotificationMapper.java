package com.pm.dm.management.mapper;

import com.pm.dm.management.dto.notification.NotificationResponseDTO;
import com.pm.dm.management.model.Notification;

public final class NotificationMapper {
    private NotificationMapper() {
    }

    public static NotificationResponseDTO toDTO(Notification notification) {
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(notification.getId().toString());
        dto.setChannel(notification.getChannel());
        dto.setType(notification.getType());
        dto.setStatus(notification.getStatus());
        dto.setRecipientEmail(notification.getRecipientEmail());
        dto.setSubject(notification.getSubject());
        dto.setScheduledAt(notification.getScheduledAt());
        dto.setSentAt(notification.getSentAt());
        dto.setFailureReason(notification.getFailureReason());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}
