package com.pm.dm.management.service;

import com.pm.dm.management.dto.common.PageResponse;
import com.pm.dm.management.dto.doctor.DoctorRequestDTO;
import com.pm.dm.management.dto.doctor.DoctorResponseDTO;
import com.pm.dm.management.exception.ConflictException;
import com.pm.dm.management.exception.ResourceNotFoundException;
import com.pm.dm.management.mapper.DoctorMapper;
import com.pm.dm.management.model.Doctor;
import com.pm.dm.management.repository.DoctorRepository;
import com.pm.dm.management.specification.DoctorSpecification;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<DoctorResponseDTO> getDoctors(String search,
                                                      String specialization,
                                                      int page,
                                                      int size,
                                                      String sortBy,
                                                      String direction) {
        var pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);
        var pageResult = doctorRepository.findAll(DoctorSpecification.search(search, specialization), pageable)
                .map(DoctorMapper::toDTO);
        return PageResponse.from(pageResult);
    }

    @Transactional(readOnly = true)
    public DoctorResponseDTO getDoctor(UUID id) {
        return DoctorMapper.toDTO(findDoctor(id));
    }

    public DoctorResponseDTO createDoctor(DoctorRequestDTO request) {
        if (doctorRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("A doctor with this email already exists: " + request.getEmail());
        }
        return DoctorMapper.toDTO(doctorRepository.save(DoctorMapper.toModel(request)));
    }

    public DoctorResponseDTO updateDoctor(UUID id, DoctorRequestDTO request) {
        Doctor doctor = findDoctor(id);
        if (doctorRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new ConflictException("A doctor with this email already exists: " + request.getEmail());
        }
        DoctorMapper.updateModel(doctor, request);
        return DoctorMapper.toDTO(doctorRepository.save(doctor));
    }

    public void deleteDoctor(UUID id) {
        Doctor doctor = findDoctor(id);
        doctor.markDeleted();
        doctorRepository.save(doctor);
    }

    @Transactional(readOnly = true)
    public long countDoctors() {
        return doctorRepository.count();
    }

    public Doctor findDoctor(UUID id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));
    }
}
