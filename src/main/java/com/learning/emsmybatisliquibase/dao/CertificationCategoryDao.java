package com.learning.emsmybatisliquibase.dao;

import com.learning.emsmybatisliquibase.entity.CertificationCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

public interface CertificationCategoryDao {

    CertificationCategory getById(@Param("id") UUID id);

    CertificationCategory getByName(@Param("name") String name);

    List<CertificationCategory> getAll();

    int insert(@Param("certificationCategory") CertificationCategory certificationCategory);

    int update(@Param("certificationCategory") CertificationCategory certificationCategory);

    int delete(@Param("id") UUID id);
}
