package com.learning.emsmybatisliquibase.dao;

import com.learning.emsmybatisliquibase.entity.Password;
import com.learning.emsmybatisliquibase.entity.PasswordStatus;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

public interface PasswordDao {
    List<Password> getByEmployeeUuidAndStatus(@Param("employeeUuid") UUID employeeUuid, @Param("status") PasswordStatus status);

    int insert(@Param("password") Password password);

    int update(@Param("password") Password password);
}
