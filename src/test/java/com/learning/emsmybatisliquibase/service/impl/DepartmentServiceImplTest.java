package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.DepartmentDao;
import com.learning.emsmybatisliquibase.dto.AddDepartmentDto;
import com.learning.emsmybatisliquibase.entity.Department;
import com.learning.emsmybatisliquibase.exception.IntegrityException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

    @InjectMocks
    DepartmentServiceImpl departmentService;

    @Mock
    DepartmentDao departmentDao;

    Department department = Department.builder()
            .uuid(UUID.randomUUID())
            .name("Dummy")
            .build();

    @Test
    void addDepartmentAlreadyExistsSuccess() {
        when(departmentDao.getByName(anyString())).thenReturn(department);

        Department departmentResponse = departmentService.add(new AddDepartmentDto(department.getName()));
        assertEquals(department, departmentResponse);

        verify(departmentDao, times(1)).getByName(anyString());
    }

    @Test
    void addDepartmentNotExistsSuccess() {
        when(departmentDao.getByName(anyString())).thenReturn(null);
        when(departmentDao.insert(any(Department.class))).thenReturn(1);

        Department departmentResponse = departmentService.add(new AddDepartmentDto(department.getName()));

        assertEquals(department.getName(), departmentResponse.getName());
        verify(departmentDao, times(1)).getByName(anyString());
        verify(departmentDao, times(1)).insert(any(Department.class));
    }

    @Test
    void addDepartmentExpect() {
        when(departmentDao.getByName(anyString())).thenReturn(null);
        when(departmentDao.insert(any(Department.class))).thenReturn(0);

        assertThrows(IntegrityException.class, () ->
                departmentService.add(new AddDepartmentDto(department.getName())));
    }

    @Test
    void updateSuccess() {
        AddDepartmentDto departmentDto = new AddDepartmentDto("Dummy1");
        when(departmentDao.get(department.getUuid())).thenReturn(department);
        department.setName(departmentDto.getName());
        when(departmentDao.update(any(Department.class))).thenReturn(1);

        Department updatedDepartment = departmentService.update(department.getUuid(), departmentDto);

        assertEquals(departmentDto.getName(), updatedDepartment.getName());
        assertEquals(department.getUuid(), updatedDepartment.getUuid());

        verify(departmentDao, times(1)).get(any(UUID.class));
    }
}