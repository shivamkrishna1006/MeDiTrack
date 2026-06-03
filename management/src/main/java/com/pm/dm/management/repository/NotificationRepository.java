package com.pm.dm.management.repository;

import com.pm.dm.management.model.Notification;
import com.pm.dm.management.model.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID>, JpaSpecificationExecutor<Notification> {
    List<Notification> findTop25ByStatusOrderByCreatedAtAsc(NotificationStatus status);
}
