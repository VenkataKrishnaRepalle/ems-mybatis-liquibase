package com.learning.emsmybatisliquibase.dao;

import com.learning.emsmybatisliquibase.entity.Skills;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

public interface SkillsDao {
    Skills getById(@Param("uuid") UUID uuid);

    List<Skills> getByEmployeeId(@Param("employeeUuid") UUID employeeUuid);

    List<Skills> getByNameAndEmployeeId(@Param("name") String name, @Param("employeeUuid") UUID employeeUuid);

    int insert(@Param("skills") Skills skills);

    int update(@Param("skills") Skills skills);

    int delete(@Param("uuid") UUID uuid);
}
