package com.learning.emsmybatisliquibase.dao;

import com.learning.emsmybatisliquibase.dto.EmployeeDetailsDto;
import com.learning.emsmybatisliquibase.dto.EmployeePaginationResponseDto;
import com.learning.emsmybatisliquibase.dto.EmployeeResponseDto;
import com.learning.emsmybatisliquibase.entity.Employee;
import com.learning.emsmybatisliquibase.entity.ProfileStatus;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface EmployeeDao {

    Employee get(@Param("uuid") UUID uuid);

    Employee getByEmail(@Param("email") String email);

    int insert(@Param("employee") Employee employee);

    int updateLeavingDate(@Param("leavingDate") Date leavingDate, @Param("employeeUuid") UUID employeeUuid);

    List<Employee> getAll();

    Long count();

    List<Employee> getAllByManagerUuid(@Param("managerUuid") UUID managerUuid);

    int update(@Param("employee") Employee employee);

    List<Employee> getActiveEmployeesWithPastLeavingDate();

    List<UUID> getAllActiveEmployeeIds(@Param("profileStatuses") List<ProfileStatus> profileStatuses);

    EmployeeResponseDto getMe(@Param("employeeUuid") UUID employeeUuid);

    EmployeePaginationResponseDto findAll(@Param("size") int size, @Param("offSet") int offSet,
                                          @Param("sortBy") String sortBy, @Param("sortOrder") String sortOrder,
                                          @Param("profileStatuses") List<ProfileStatus> statuses);

    Long employeesCount(@Param("profileStatuses") List<ProfileStatus> statuses);

    List<EmployeeDetailsDto> getAllActiveManagers();

    List<EmployeeDetailsDto> getByNameOrEmail(@Param("name") String name);
}