package com.pm.dm.management.repository;

import com.pm.dm.management.model.Appointment;
import com.pm.dm.management.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID>, JpaSpecificationExecutor<Appointment> {

    @Query("""
            select case when count(a) > 0 then true else false end
            from Appointment a
            where a.doctor.id = :doctorId
              and a.status <> :cancelledStatus
              and (:excludeId is null or a.id <> :excludeId)
              and a.appointmentTime < :endTime
              and a.endTime > :startTime
            """)
    boolean existsConflict(@Param("doctorId") UUID doctorId,
                           @Param("startTime") LocalDateTime startTime,
                           @Param("endTime") LocalDateTime endTime,
                           @Param("cancelledStatus") AppointmentStatus cancelledStatus,
                           @Param("excludeId") UUID excludeId);

    long countByAppointmentTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("select a.status as status, count(a) as count from Appointment a group by a.status")
    List<StatusCount> countByStatus();

    interface StatusCount {
        AppointmentStatus getStatus();
        long getCount();
    }
}
