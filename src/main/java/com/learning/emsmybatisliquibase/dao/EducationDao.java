package com.learning.emsmybatisliquibase.dao;

import com.learning.emsmybatisliquibase.entity.Education;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

public interface EducationDao {
    int insert(@Param("education") Education education);

    List<Education> getAllByEmployeeUuid(@Param("employeeUuid") UUID employeeUuid);

    int delete(@Param("uuid") UUID uuid);

    Education get(@Param("uuid") UUID uuid);

    int update(@Param("education") Education education);
}
