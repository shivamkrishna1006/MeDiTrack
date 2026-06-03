package com.pm.dm.management.service;

import com.pm.dm.management.dto.common.PageResponse;
import com.pm.dm.management.dto.notification.NotificationRequestDTO;
import com.pm.dm.management.dto.notification.NotificationResponseDTO;
import com.pm.dm.management.mapper.NotificationMapper;
import com.pm.dm.management.model.Notification;
import com.pm.dm.management.model.NotificationChannel;
import com.pm.dm.management.model.NotificationStatus;
import com.pm.dm.management.model.NotificationType;
import com.pm.dm.management.repository.NotificationRepository;
import com.pm.dm.management.specification.NotificationSpecification;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final AuditLogService auditLogService;

    public NotificationService(NotificationRepository notificationRepository,
                               ObjectProvider<JavaMailSender> mailSenderProvider,
                               AuditLogService auditLogService) {
        this.notificationRepository = notificationRepository;
        this.mailSenderProvider = mailSenderProvider;
        this.auditLogService = auditLogService;
    }

    public NotificationResponseDTO queue(NotificationRequestDTO request) {
        Notification notification = new Notification();
        notification.setChannel(NotificationChannel.EMAIL);
        notification.setType(request.getType());
        notification.setStatus(NotificationStatus.PENDING);
        notification.setRecipientEmail(request.getRecipientEmail());
        notification.setSubject(request.getSubject());
        notification.setBody(request.getBody());
        notification.setScheduledAt(request.getScheduledAt() != null ? request.getScheduledAt() : LocalDateTime.now());
        notification.setRelatedEntityId(request.getRelatedEntityId());
        notification.setRelatedEntityType(request.getRelatedEntityType());
        Notification saved = notificationRepository.save(notification);
        auditLogService.log("NOTIFICATION_QUEUED", "Notification", saved.getId(), saved.getSubject());
        return NotificationMapper.toDTO(saved);
    }

    public void queueSystemEmail(NotificationType type, String email, String subject, String body, UUID entityId, String entityType) {
        NotificationRequestDTO request = new NotificationRequestDTO();
        request.setType(type);
        request.setRecipientEmail(email);
        request.setSubject(subject);
        request.setBody(body);
        request.setRelatedEntityId(entityId);
        request.setRelatedEntityType(entityType);
        queue(request);
    }

    @Transactional(readOnly = true)
    public PageResponse<NotificationResponseDTO> getNotifications(NotificationStatus status,
                                                                  NotificationType type,
                                                                  String recipientEmail,
                                                                  int page,
                                                                  int size) {
        var pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        var result = notificationRepository.findAll(NotificationSpecification.search(status, type, recipientEmail), pageable)
                .map(NotificationMapper::toDTO);
        return PageResponse.from(result);
    }

    public void sendPendingNow() {
        notificationRepository.findTop25ByStatusOrderByCreatedAtAsc(NotificationStatus.PENDING)
                .stream()
                .filter(notification -> notification.getScheduledAt() == null || !notification.getScheduledAt().isAfter(LocalDateTime.now()))
                .forEach(this::send);
    }

    @Scheduled(fixedDelay = 60000)
    public void scheduledSend() {
        sendPendingNow();
    }

    private void send(Notification notification) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            log.info("Notification queued for {}: {}", notification.getRecipientEmail(), notification.getSubject());
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false);
            helper.setTo(notification.getRecipientEmail());
            helper.setSubject(notification.getSubject());
            helper.setText(notification.getBody(), false);
            mailSender.send(message);
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
        } catch (MessagingException | RuntimeException ex) {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setFailureReason(ex.getMessage());
        }
        notificationRepository.save(notification);
    }
}
