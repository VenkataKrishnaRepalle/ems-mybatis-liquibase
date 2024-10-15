package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.CertificationCategoryDao;
import com.learning.emsmybatisliquibase.dto.CertificationCategoryDto;
import com.learning.emsmybatisliquibase.entity.CertificationCategory;
import com.learning.emsmybatisliquibase.exception.FoundException;
import com.learning.emsmybatisliquibase.exception.IntegrityException;
import com.learning.emsmybatisliquibase.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.UUID;


@ExtendWith(MockitoExtension.class)
class CertificationCategoryServiceImplTest {

    @InjectMocks
    CertificationCategoryServiceImpl certificationCategoryService;

    @Mock
    CertificationCategoryDao certificationCategoryDao;

    @Test
    void getById_success() {
        CertificationCategory certificationCategory = CertificationCategory.builder()
                .uuid(UUID.randomUUID())
                .build();
        when(certificationCategoryDao.getById(certificationCategory.getUuid())).thenReturn(certificationCategory);

        CertificationCategory result = certificationCategoryService.getById(certificationCategory.getUuid());
        assertNotNull(result);
        assertEquals(certificationCategory, result);

        verify(certificationCategoryDao).getById(certificationCategory.getUuid());
    }

    @Test
    void getById_notFound() {
        when(certificationCategoryDao.getById(any(UUID.class))).thenReturn(null);

        assertThrows(NotFoundException.class, () ->
                certificationCategoryService.getById(UUID.randomUUID()));
    }

    @Test
    void getAll_success() {
        CertificationCategory certificationCategory = CertificationCategory.builder()
                .uuid(UUID.randomUUID())
                .build();
        when(certificationCategoryDao.getAll()).thenReturn(List.of(certificationCategory));

        List<CertificationCategory> result = certificationCategoryService.getAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(certificationCategory, result.get(0));

        verify(certificationCategoryDao).getAll();
    }

    @Test
    void add_success() {
        String name = "test certification";
        CertificationCategoryDto certificationCategoryDto = CertificationCategoryDto.builder()
                .name(name)
                .build();
        CertificationCategory certificationCategory = CertificationCategory.builder()
                .uuid(UUID.randomUUID())
                .name(name)
                .build();
        when(certificationCategoryDao.getByName(name)).thenReturn(null);
        when(certificationCategoryDao.insert(any(CertificationCategory.class))).thenReturn(1);

        CertificationCategory result = certificationCategoryService.add(certificationCategoryDto);
        assertNotNull(result);
        assertEquals(certificationCategory.getName(), result.getName());

        verify(certificationCategoryDao, times(1)).getByName(name);
        verify(certificationCategoryDao, times(1)).insert(any());
    }

    @Test
    void add_nameAlreadyExists() {
        String name = "test certification";
        CertificationCategoryDto certificationCategoryDto = CertificationCategoryDto.builder()
                .name(name)
                .build();
        CertificationCategory certificationCategory = CertificationCategory.builder()
                .uuid(UUID.randomUUID())
                .name(name)
                .build();
        when(certificationCategoryDao.getByName(name)).thenReturn(certificationCategory);

        assertThrows(FoundException.class, () ->
                certificationCategoryService.add(certificationCategoryDto));
    }

    @Test
    void add_creationFailes() {
        String name = "test certification";
        CertificationCategoryDto certificationCategoryDto = CertificationCategoryDto.builder()
                .name(name)
                .build();
        when(certificationCategoryDao.getByName(name)).thenReturn(null);
        when(certificationCategoryDao.insert(any(CertificationCategory.class))).thenReturn(0);

        assertThrows(IntegrityException.class, () ->
                certificationCategoryService.add(certificationCategoryDto));
    }

    @Test
    void update_success() {
        UUID certificationCategoryUuid = UUID.randomUUID();
        CertificationCategory certificationCategoryExists = CertificationCategory.builder()
                .uuid(certificationCategoryUuid)
                .name("test")
                .build();
        CertificationCategory certificationCategoryUpdate = CertificationCategory.builder()
                .uuid(certificationCategoryUuid)
                .name(" test ")
                .build();

        when(certificationCategoryDao.getById(certificationCategoryUuid)).thenReturn(certificationCategoryExists);
        when(certificationCategoryDao.update(certificationCategoryExists)).thenReturn(1);

        CertificationCategory result = certificationCategoryService.update(certificationCategoryUuid, certificationCategoryUpdate);

        assertNotNull(result);
        assertEquals(certificationCategoryUpdate.getName().trim(), result.getName());  // Compare trimmed name
        assertEquals(certificationCategoryUuid, result.getUuid());

        verify(certificationCategoryDao, times(1)).getById(certificationCategoryUuid);
        verify(certificationCategoryDao, times(1)).update(certificationCategoryExists);
    }

    @Test
    void update_failedToUpdate() {
        UUID certificationCategoryUuid = UUID.randomUUID();
        CertificationCategory certificationCategory = CertificationCategory.builder()
                .uuid(certificationCategoryUuid)
                .name("test")
                .build();
        CertificationCategory certificationCategoryUpdate = CertificationCategory.builder()
                .uuid(certificationCategoryUuid)
                .name(" test ")
                .build();
        when(certificationCategoryDao.getById(certificationCategoryUuid)).thenReturn(certificationCategory);
        when(certificationCategoryDao.update(any(CertificationCategory.class))).thenReturn(0);

        assertThrows(IntegrityException.class, () ->
                certificationCategoryService.update(certificationCategoryUuid, certificationCategoryUpdate));

        verify(certificationCategoryDao, times(1)).getById(certificationCategoryUuid);
        verify(certificationCategoryDao, times(1)).update(any());
    }

    @Test
    void delete_success() {
        UUID certificationCategoryUuid = UUID.randomUUID();
        CertificationCategory certificationCategory = CertificationCategory.builder()
                .uuid(certificationCategoryUuid)
                .name("test")
                .build();
        when(certificationCategoryDao.getById(certificationCategoryUuid)).thenReturn(certificationCategory);
        when(certificationCategoryDao.delete(certificationCategoryUuid)).thenReturn(1);

        assertDoesNotThrow(() -> certificationCategoryService.delete(certificationCategoryUuid));

        verify(certificationCategoryDao, times(1)).getById(certificationCategoryUuid);
        verify(certificationCategoryDao, times(1)).delete(certificationCategoryUuid);
    }

    @Test
    void delete_failedToDelete() {
        UUID certificationCategoryUuid = UUID.randomUUID();
        CertificationCategory certificationCategory = CertificationCategory.builder()
                .uuid(certificationCategoryUuid)
                .name("test")
                .build();
        when(certificationCategoryDao.getById(certificationCategoryUuid)).thenReturn(certificationCategory);
        when(certificationCategoryDao.delete(certificationCategoryUuid)).thenReturn(0);

        assertThrows(IntegrityException.class, () ->
                certificationCategoryService.delete(certificationCategoryUuid));

        verify(certificationCategoryDao, times(1)).delete(certificationCategoryUuid);
        verify(certificationCategoryDao, times(1)).delete(certificationCategoryUuid);

    }
}