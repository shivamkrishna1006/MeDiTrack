package com.pm.dm.management.repository;

import com.pm.dm.management.model.BillingRecord;
import com.pm.dm.management.model.BillingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BillingRecordRepository extends JpaRepository<BillingRecord, UUID>, JpaSpecificationExecutor<BillingRecord> {
    boolean existsByInvoiceNumber(String invoiceNumber);
    boolean existsByInvoiceNumberAndIdNot(String invoiceNumber, UUID id);
    long countByStatusAndDueDateBefore(BillingStatus status, LocalDate dueDate);

    @Query("select coalesce(sum(b.amount), 0) from BillingRecord b where b.status = :status")
    BigDecimal sumAmountByStatus(BillingStatus status);

    @Query("select b.status as status, count(b) as count from BillingRecord b group by b.status")
    List<StatusCount> countByStatus();

    interface StatusCount {
        BillingStatus getStatus();
        long getCount();
    }
}
