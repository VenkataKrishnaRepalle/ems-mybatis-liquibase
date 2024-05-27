package com.learning.emsmybatisliquibase.dao;

import com.learning.emsmybatisliquibase.entity.Experience;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

public interface ExperienceDao {
    Experience getById(@Param("uuid") UUID uuid);

    List<Experience> getByEmployeeUuid(@Param("employeeUuid") UUID employeeUuid);

    int insert(@Param("experience") Experience experience);

    int update(@Param("experience") Experience experience);

    int delete(@Param("uuid") UUID uuid);
}
