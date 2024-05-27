package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.ExperienceDao;
import com.learning.emsmybatisliquibase.entity.Experience;
import com.learning.emsmybatisliquibase.exception.FoundException;
import com.learning.emsmybatisliquibase.exception.IntegrityException;
import com.learning.emsmybatisliquibase.exception.InvalidInputException;
import com.learning.emsmybatisliquibase.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExperienceServiceImplTest {

    @Mock
    private ExperienceDao experienceDao;

    @InjectMocks
    private ExperienceServiceImpl experienceService;

    private Experience experience;

    @BeforeEach
    void setUp() {
        experience = Experience.builder()
                .uuid(UUID.randomUUID())
                .employeeUuid(UUID.randomUUID())
                .companyName("tesco")
                .isCurrentJob(Boolean.TRUE)
                .startDate(new Date(2000, Calendar.MAY, 10))
                .build();
    }

    @Test
    void testGetById_Expect() {
        when(experienceDao.getById(any(UUID.class))).thenReturn(null);

        assertThrows(NotFoundException.class, () ->
                experienceService.getById(experience.getUuid()));
    }

    @Test
    void testGetById_Success() {
        when(experienceDao.getById(any(UUID.class))).thenReturn(experience);

        Experience result = experienceService.getById(experience.getUuid());
        assertEquals(result, experience);
    }

    @Test
    void testGetAllByEmployeeUuid() {
        List<Experience> experiences = List.of(experience);
        when(experienceDao.getByEmployeeUuid(any(UUID.class))).thenReturn(experiences);

        List<Experience> result = experienceService.getAllByEmployeeUuid(experience.getEmployeeUuid());

        assertEquals(result, experiences);
    }

    @Test
    void testAddSuccess() {
        Experience experience1 = Experience.builder()
                .uuid(UUID.randomUUID())
                .employeeUuid(experience.getEmployeeUuid())
                .startDate(new Date())
                .endDate(new Date())
                .build();
        List<Experience> experiences = List.of(experience, experience1);

        when(experienceDao.getByEmployeeUuid(any(UUID.class))).thenReturn(null);
        when(experienceDao.insert(experience)).thenReturn(1);
        when(experienceDao.insert(experience1)).thenReturn(1);

        experienceService.add(experience.getEmployeeUuid(), experiences);

        verify(experienceDao, times(1)).getByEmployeeUuid(any(UUID.class));
        verify(experienceDao, times(2)).insert(any(Experience.class));
    }

    @Test
    void testAddInputNull() {
        InvalidInputException exception = assertThrows(InvalidInputException.class, () ->
                experienceService.add(UUID.randomUUID(), null));

        assertEquals("EXPERIENCE_INPUT_NULL", exception.getErrorCode());
        assertEquals("experiences input is null", exception.getDynamicValue());
    }

    @Test
    void testAddIntegrityException() {
        List<Experience> experiences = List.of(experience);

        when(experienceDao.getByEmployeeUuid(any(UUID.class))).thenReturn(null);
        when(experienceDao.insert(experience)).thenReturn(0);

        assertThrows(IntegrityException.class, () ->
                experienceService.add(experience.getEmployeeUuid(), experiences));

    }

    @Test
    void testUpdateSuccess() {
        Experience experience1 = Experience.builder()
                .uuid(UUID.randomUUID())
                .employeeUuid(experience.getEmployeeUuid())
                .startDate(new Date())
                .endDate(new Date())
                .build();
        List<Experience> experiences = List.of(experience, experience1);
        when(experienceDao.getById(any(UUID.class))).thenReturn(experience1);
        when(experienceDao.update(experience)).thenReturn(1);
        when(experienceDao.update(experience1)).thenReturn(1);

        experienceService.update(experience.getUuid(), experiences);

        verify(experienceDao, times(2)).getById(any(UUID.class));
        verify(experienceDao, times(2)).update(any(Experience.class));
    }

    @Test
    void testUpdateInputNull() {
        InvalidInputException exception = assertThrows(InvalidInputException.class, () ->
                experienceService.update(UUID.randomUUID(), null));

        assertEquals("EXPERIENCE_INPUT_NULL", exception.getErrorCode());
        assertEquals("experiences input is null", exception.getDynamicValue());
    }

    @Test
    void testUpdateIntegrityException() {
        List<Experience> experiences = List.of(experience);

        when(experienceDao.getById(any(UUID.class))).thenReturn(experience);
        when(experienceDao.update(experience)).thenReturn(0);

        assertThrows(IntegrityException.class, () ->
                experienceService.update(experience.getEmployeeUuid(), experiences));

    }

    @Test
    void testDeleteById() {
        when(experienceDao.getById(any(UUID.class))).thenReturn(experience).thenReturn(null);
        when(experienceDao.delete(any(UUID.class))).thenReturn(1);

        experienceService.deleteById(experience.getUuid());

        verify(experienceDao, times(2)).getById(any(UUID.class));
        verify(experienceDao, times(1)).delete(any(UUID.class));
    }

    @Test
    void testDeleteByIdExperienceNotFound() {
        when(experienceDao.getById(any(UUID.class))).thenReturn(null);

        assertThrows(NotFoundException.class, () ->
                experienceService.deleteById(experience.getUuid()));
    }

    @Test
    void testDeleteByIdIntegrityException() {
        when(experienceDao.getById(any(UUID.class))).thenReturn(experience);
        when(experienceDao.delete(any(UUID.class))).thenReturn(0);

        assertThrows(IntegrityException.class, () ->
                experienceService.deleteById(experience.getUuid()));

        verify(experienceDao, times(1)).getById(any(UUID.class));
        verify(experienceDao, times(1)).delete(any(UUID.class));
    }

    @Test
    void testDeleteByIdExperienceStillExistsAfterDeletion() {
        when(experienceDao.getById(any(UUID.class))).thenReturn(experience).thenReturn(experience);
        when(experienceDao.delete(any(UUID.class))).thenReturn(1);

        assertThrows(FoundException.class, () ->
                experienceService.deleteById(experience.getUuid()));

        verify(experienceDao, times(2)).getById(any(UUID.class));
        verify(experienceDao, times(1)).delete(any(UUID.class));

    }

}