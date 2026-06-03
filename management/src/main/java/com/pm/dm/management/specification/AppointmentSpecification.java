package com.pm.dm.management.specification;

import com.pm.dm.management.model.Appointment;
import com.pm.dm.management.model.AppointmentStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.UUID;

public final class AppointmentSpecification {
    private AppointmentSpecification() {
    }

    public static Specification<Appointment> search(UUID patientId,
                                                    UUID doctorId,
                                                    AppointmentStatus status,
                                                    LocalDateTime from,
                                                    LocalDateTime to) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();
            if (patientId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("patient").get("id"), patientId));
            }
            if (doctorId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("doctor").get("id"), doctorId));
            }
            if (status != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), status));
            }
            if (from != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("appointmentTime"), from));
            }
            if (to != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("appointmentTime"), to));
            }
            return predicate;
        };
    }
}
