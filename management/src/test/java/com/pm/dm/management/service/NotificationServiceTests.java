package com.pm.dm.management.service;

import com.pm.dm.management.dto.notification.NotificationRequestDTO;
import com.pm.dm.management.model.NotificationStatus;
import com.pm.dm.management.model.NotificationType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@Transactional
class NotificationServiceTests {

    @Autowired
    private NotificationService notificationService;

    @Test
    void shouldQueueNotification() {
        NotificationRequestDTO request = new NotificationRequestDTO();
        request.setType(NotificationType.APPOINTMENT_REMINDER);
        request.setRecipientEmail("john.doe@example.com");
        request.setSubject("Reminder");
        request.setBody("Your appointment is coming up.");

        var queued = notificationService.queue(request);

        assertThat(queued.getStatus()).isEqualTo(NotificationStatus.PENDING);
        assertThat(queued.getRecipientEmail()).isEqualTo("john.doe@example.com");
    }
}
