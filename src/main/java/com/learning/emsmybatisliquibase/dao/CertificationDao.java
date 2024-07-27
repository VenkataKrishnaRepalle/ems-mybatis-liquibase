package com.learning.emsmybatisliquibase.dao;

import com.learning.emsmybatisliquibase.entity.Certification;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

public interface CertificationDao {

    Certification getById(@Param("id") UUID id);

    List<Certification> getByEmployeeUuid(@Param("employeeUuid") UUID employeeUuid);

    int insert(@Param("certification") Certification certification);

    int update(@Param("certification") Certification certification);

    int delete(@Param("id") UUID id);

    Certification getByNameAndEmployeeUuid(@Param("name") String name, @Param("employeeUuid") UUID employeeUuid);

    List<Certification> getByCertificationCategoryUuid(@Param("certificationCategoryUuid") UUID certificationCategoryUuid);
}
