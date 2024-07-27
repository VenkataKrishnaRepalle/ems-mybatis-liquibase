package com.learning.emsmybatisliquibase.service;

import com.learning.emsmybatisliquibase.dto.CertificationCategoryDto;
import com.learning.emsmybatisliquibase.entity.CertificationCategory;

import java.util.List;
import java.util.UUID;

public interface CertificationCategoryService {
    CertificationCategory add(CertificationCategoryDto certificationCategoryDto);

    CertificationCategory update(UUID id, CertificationCategoryDto certificationCategoryDto);

    CertificationCategory getById(UUID id);

    List<CertificationCategory> getAll();

    void delete(UUID id);
}
