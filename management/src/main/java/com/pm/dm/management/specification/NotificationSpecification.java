package com.pm.dm.management.specification;

import com.pm.dm.management.model.Notification;
import com.pm.dm.management.model.NotificationStatus;
import com.pm.dm.management.model.NotificationType;
import org.springframework.data.jpa.domain.Specification;

public final class NotificationSpecification {
    private NotificationSpecification() {
    }

    public static Specification<Notification> search(NotificationStatus status, NotificationType type, String recipientEmail) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();
            if (status != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), status));
            }
            if (type != null) {
                predicate = cb.and(predicate, cb.equal(root.get("type"), type));
            }
            if (recipientEmail != null && !recipientEmail.isBlank()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("recipientEmail")), "%" + recipientEmail.toLowerCase() + "%"));
            }
            return predicate;
        };
    }
}
