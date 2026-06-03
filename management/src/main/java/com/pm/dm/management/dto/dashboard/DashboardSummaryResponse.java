package com.pm.dm.management.dto.dashboard;

import java.math.BigDecimal;
import java.util.Map;

public class DashboardSummaryResponse {
    private Map<String, Long> totals;
    private Map<String, Long> appointmentsByStatus;
    private Map<String, Long> billingByStatus;
    private long newPatientsThisMonth;
    private long upcomingAppointments;
    private long overdueBills;
    private BigDecimal paidRevenue;
    private BigDecimal pendingRevenue;

    public Map<String, Long> getTotals() { return totals; }
    public void setTotals(Map<String, Long> totals) { this.totals = totals; }
    public Map<String, Long> getAppointmentsByStatus() { return appointmentsByStatus; }
    public void setAppointmentsByStatus(Map<String, Long> appointmentsByStatus) { this.appointmentsByStatus = appointmentsByStatus; }
    public Map<String, Long> getBillingByStatus() { return billingByStatus; }
    public void setBillingByStatus(Map<String, Long> billingByStatus) { this.billingByStatus = billingByStatus; }
    public long getNewPatientsThisMonth() { return newPatientsThisMonth; }
    public void setNewPatientsThisMonth(long newPatientsThisMonth) { this.newPatientsThisMonth = newPatientsThisMonth; }
    public long getUpcomingAppointments() { return upcomingAppointments; }
    public void setUpcomingAppointments(long upcomingAppointments) { this.upcomingAppointments = upcomingAppointments; }
    public long getOverdueBills() { return overdueBills; }
    public void setOverdueBills(long overdueBills) { this.overdueBills = overdueBills; }
    public BigDecimal getPaidRevenue() { return paidRevenue; }
    public void setPaidRevenue(BigDecimal paidRevenue) { this.paidRevenue = paidRevenue; }
    public BigDecimal getPendingRevenue() { return pendingRevenue; }
    public void setPendingRevenue(BigDecimal pendingRevenue) { this.pendingRevenue = pendingRevenue; }
}
