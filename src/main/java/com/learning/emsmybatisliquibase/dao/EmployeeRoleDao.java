package com.learning.emsmybatisliquibase.dao;

import com.learning.emsmybatisliquibase.entity.EmployeeRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

public interface EmployeeRoleDao {
    int insert(@Param("employeeRole") EmployeeRole employeeRole);

    int delete(@Param("employeeUuid") UUID employeeUuid, @Param("role") String role);

    List<EmployeeRole> getByEmployeeUuid(@Param("employeeUuid") UUID employeeUuid);
}
