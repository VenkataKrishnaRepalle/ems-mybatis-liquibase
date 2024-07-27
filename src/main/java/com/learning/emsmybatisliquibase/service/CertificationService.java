package com.learning.emsmybatisliquibase.service;

import com.learning.emsmybatisliquibase.dto.CertificationRequestDto;
import com.learning.emsmybatisliquibase.dto.CertificationResponseDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CertificationService {

    List<CertificationResponseDto> getAllByEmployeeUuid(UUID employeeUuid);

    CertificationResponseDto add(CertificationRequestDto addCertificationDto);

    CertificationResponseDto getById(UUID certificationUuid);

    CertificationResponseDto update(UUID certificationUuid, CertificationRequestDto updateCertificationDto);

    void delete(UUID certificationUuid);

    Map<UUID, String> getByCertificationCategory(UUID certificationCategoryUuid);
}
