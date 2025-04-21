package com.learning.emsmybatisliquibase.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.emsmybatisliquibase.entity.Experience;
import com.learning.emsmybatisliquibase.service.ExperienceService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class ExperienceControllerTest {
    MockMvc mockMvc;

    @Mock
    private ExperienceService experienceService;

    @InjectMocks
    private ExperienceController experienceController;

    ObjectMapper objectMapper = new ObjectMapper();

    private Experience experience;

    private final String EXPERIENCE_PATH = "/api/experience";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(experienceController).build();

        experience = Experience.builder()
                .uuid(UUID.randomUUID())
                .employeeUuid(UUID.randomUUID())
                .companyName("tesco")
                .build();
    }

    @Test
    void testGetById() throws Exception {
        when(experienceService.getById(any(UUID.class))).thenReturn(experience);

        mockMvc.perform(get(EXPERIENCE_PATH + "/getById/{experienceId}", experience.getUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(experience.getUuid().toString()))
                .andExpect(jsonPath("$.employeeUuid").value(experience.getEmployeeUuid().toString()));
    }

    @Test
    void testGetAllByEmployeeUuid() throws Exception {
        List<Experience> experiences = List.of(experience);
        when(experienceService.getAllByEmployeeUuid(any(UUID.class))).thenReturn(experiences);

        mockMvc.perform(get(EXPERIENCE_PATH + "/getAll/employee/{employeeId}", experience.getEmployeeUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uuid").value(experience.getUuid().toString()))
                .andExpect(jsonPath("$[0].employeeUuid").value(experience.getEmployeeUuid().toString()));
    }

    @Test
    void testAdd() throws Exception {
        List<Experience> experiences = List.of(experience);
        when(experienceService.add(any(UUID.class), any())).thenReturn(experiences);

        mockMvc.perform(post(EXPERIENCE_PATH + "/add/employee/{employeeId}", experience.getEmployeeUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(experiences))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].employeeUuid").value(experience.getEmployeeUuid().toString()));
    }

    @Test
    void testUpdate() throws Exception {
        List<Experience> experiences = List.of(experience);
        when(experienceService.update(any(UUID.class), any())).thenReturn(experiences);

        mockMvc.perform(put(EXPERIENCE_PATH + "/update/employee/{employeeId}", experience.getEmployeeUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(experiences))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$[0].employeeUuid").value(experience.getEmployeeUuid().toString()));
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(experienceService).deleteById(any(UUID.class));

        mockMvc.perform(delete(EXPERIENCE_PATH + "/deleteById/{experienceId}", experience.getUuid()))
                .andExpect(status().isNoContent());
    }
}