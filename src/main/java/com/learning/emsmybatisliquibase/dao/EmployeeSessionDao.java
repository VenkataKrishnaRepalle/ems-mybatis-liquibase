package com.learning.emsmybatisliquibase.dao;

import com.learning.emsmybatisliquibase.entity.EmployeeSession;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

public interface EmployeeSessionDao {
    int insert(@Param("session") EmployeeSession employeeSession);

    EmployeeSession getById(@Param("uuid") UUID uuid);

    List<EmployeeSession> getByEmployeeUuid(@Param("employeeUuid") UUID employeeUuid);

    List<EmployeeSession> getByEmployeeUuidAndStatus(@Param("employeeUuid") UUID employeeUuid,
                                                     @Param("isActive") Boolean isActive);

    int update(@Param("session") EmployeeSession employeeSession);

    int delete(@Param("uuid") UUID uuid);
}
