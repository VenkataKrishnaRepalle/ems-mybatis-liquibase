package com.learning.emsmybatisliquibase.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.emsmybatisliquibase.dto.CertificationCategoryDto;
import com.learning.emsmybatisliquibase.entity.CertificationCategory;
import com.learning.emsmybatisliquibase.service.CertificationCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CertificationCategoryControllerTest {

    MockMvc mockMvc;

    @InjectMocks
    CertificationCategoryController certificationCategoryController;

    @Mock
    CertificationCategoryService certificationCategoryService;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(certificationCategoryController).build();
    }

    @Test
    void testGetById() throws Exception {
        UUID certificationCategoryUuid = UUID.randomUUID();
        CertificationCategory certificationCategory = new CertificationCategory(certificationCategoryUuid, "test");

        when(certificationCategoryService.getById(certificationCategoryUuid)).thenReturn(certificationCategory);

        mockMvc.perform(get("/api/certification-category/get/{id}", certificationCategoryUuid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(certificationCategoryService, times(1)).getById(certificationCategoryUuid);
    }

    @Test
    void testGetAll() throws Exception {
        List<CertificationCategory> certificationCategories = List.of(
                new CertificationCategory(UUID.randomUUID(), "test1"),
                new CertificationCategory(UUID.randomUUID(), "test2")
        );

        when(certificationCategoryService.getAll()).thenReturn(certificationCategories);

        mockMvc.perform(get("/api/certification-category/getAll")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(certificationCategoryService, times(1)).getAll();
    }

    @Test
    void testAdd() throws Exception {
        String certificationName = "test certification";
        CertificationCategory certificationCategory = new CertificationCategory(UUID.randomUUID(), certificationName);
        CertificationCategoryDto certificationCategoryDto = new CertificationCategoryDto(certificationName);

        when(certificationCategoryService.add(certificationCategoryDto)).thenReturn(certificationCategory);

        mockMvc.perform(post("/api/certification-category/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(certificationCategoryDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(certificationName));

        verify(certificationCategoryService, times(1)).add(certificationCategoryDto);
    }

    @Test
    void testUpdate() throws Exception {
        UUID certificationUuid = UUID.randomUUID();
        String certificationName = "test certification";
        CertificationCategory certificationCategory = new CertificationCategory(certificationUuid, certificationName);

        when(certificationCategoryService.update(certificationUuid, certificationCategory)).thenReturn(certificationCategory);

        mockMvc.perform(put("/api/certification-category/update/{id}", certificationUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(certificationCategory)))
                .andExpect(status().isAccepted());

        verify(certificationCategoryService, times(1)).update(certificationUuid, certificationCategory);
    }

    @Test
    void testDelete() throws Exception {
        UUID uuid = UUID.randomUUID();

        doNothing().when(certificationCategoryService).delete(uuid);

        mockMvc.perform(delete("/api/certification-category/delete/{id}", uuid))
                .andExpect(status().isNoContent());

        verify(certificationCategoryService, times(1)).delete(uuid);
    }
}