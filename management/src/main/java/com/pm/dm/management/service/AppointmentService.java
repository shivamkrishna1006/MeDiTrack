package com.pm.dm.management.service;

import com.pm.dm.management.dto.appointment.AppointmentRequestDTO;
import com.pm.dm.management.dto.appointment.AppointmentResponseDTO;
import com.pm.dm.management.dto.common.PageResponse;
import com.pm.dm.management.exception.BadRequestException;
import com.pm.dm.management.exception.ResourceNotFoundException;
import com.pm.dm.management.mapper.AppointmentMapper;
import com.pm.dm.management.model.Appointment;
import com.pm.dm.management.model.AppointmentStatus;
import com.pm.dm.management.repository.AppointmentRepository;
import com.pm.dm.management.specification.AppointmentSpecification;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientService patientService,
                              DoctorService doctorService,
                              AuditLogService auditLogService,
                              NotificationService notificationService) {
        this.appointmentRepository = appointmentRepository;
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.auditLogService = auditLogService;
        this.notificationService = notificationService;
    }

    @Transactional(readOnly = true)
    public PageResponse<AppointmentResponseDTO> getAppointments(UUID patientId,
                                                                UUID doctorId,
                                                                AppointmentStatus status,
                                                                LocalDateTime from,
                                                                LocalDateTime to,
                                                                int page,
                                                                int size,
                                                                String sortBy,
                                                                String direction) {
        var pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);
        var pageResult = appointmentRepository.findAll(AppointmentSpecification.search(patientId, doctorId, status, from, to), pageable)
                .map(AppointmentMapper::toDTO);
        return PageResponse.from(pageResult);
    }

    @Transactional(readOnly = true)
    public AppointmentResponseDTO getAppointment(UUID id) {
        return AppointmentMapper.toDTO(findAppointmentEntity(id));
    }

    public AppointmentResponseDTO createAppointment(AppointmentRequestDTO request) {
        validateAppointmentWindow(request, null);
        Appointment appointment = new Appointment();
        applyRequest(appointment, request);
        Appointment saved = appointmentRepository.save(appointment);
        auditLogService.log("APPOINTMENT_CREATED", "Appointment", saved.getId(), saved.getAppointmentTime().toString());
        notificationService.queueSystemEmail(com.pm.dm.management.model.NotificationType.APPOINTMENT_CONFIRMATION,
                saved.getPatient().getEmail(),
                "Appointment confirmed",
                "Your appointment is confirmed for " + saved.getAppointmentTime(),
                saved.getId(),
                "Appointment");
        return AppointmentMapper.toDTO(saved);
    }

    public AppointmentResponseDTO updateAppointment(UUID id, AppointmentRequestDTO request) {
        Appointment appointment = findAppointmentEntity(id);
        validateAppointmentWindow(request, id);
        applyRequest(appointment, request);
        Appointment updated = appointmentRepository.save(appointment);
        auditLogService.log("APPOINTMENT_UPDATED", "Appointment", updated.getId(), updated.getAppointmentTime().toString());
        return AppointmentMapper.toDTO(updated);
    }

    public void deleteAppointment(UUID id) {
        Appointment appointment = findAppointmentEntity(id);
        appointment.markDeleted();
        appointmentRepository.save(appointment);
        auditLogService.log("APPOINTMENT_CANCELLED", "Appointment", id, appointment.getAppointmentTime().toString());
    }

    @Transactional(readOnly = true)
    public long countAppointments() {
        return appointmentRepository.count();
    }

    @Transactional(readOnly = true)
    public long countUpcomingAppointments() {
        return appointmentRepository.countByAppointmentTimeBetween(java.time.LocalDateTime.now(), java.time.LocalDateTime.now().plusDays(7));
    }

    @Transactional(readOnly = true)
    public java.util.Map<String, Long> countAppointmentsByStatus() {
        return appointmentRepository.countByStatus()
                .stream()
                .collect(java.util.stream.Collectors.toMap(row -> row.getStatus().name(), com.pm.dm.management.repository.AppointmentRepository.StatusCount::getCount));
    }

    @Transactional(readOnly = true)
    public Appointment findAppointmentEntity(UUID id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
    }

    private void applyRequest(Appointment appointment, AppointmentRequestDTO request) {
        appointment.setPatient(patientService.findPatient(request.getPatientId()));
        appointment.setDoctor(doctorService.findDoctor(request.getDoctorId()));
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setDurationMinutes(request.getDurationMinutes());
        appointment.setEndTime(request.getAppointmentTime().plusMinutes(request.getDurationMinutes()));
        appointment.setStatus(request.getStatus());
        appointment.setNotes(request.getNotes());
    }

    private void validateAppointmentWindow(AppointmentRequestDTO request, UUID excludeId) {
        if (request.getStatus() != AppointmentStatus.CANCELLED) {
            LocalDateTime endTime = request.getAppointmentTime().plusMinutes(request.getDurationMinutes());
            boolean conflict = appointmentRepository.existsConflict(
                    request.getDoctorId(),
                    request.getAppointmentTime(),
                    endTime,
                    AppointmentStatus.CANCELLED,
                    excludeId
            );
            if (conflict) {
                throw new BadRequestException("Doctor already has an appointment in this time window");
            }
        }
    }
}
