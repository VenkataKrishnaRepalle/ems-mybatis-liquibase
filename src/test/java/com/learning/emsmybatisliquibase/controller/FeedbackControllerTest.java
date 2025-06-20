package com.learning.emsmybatisliquibase.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.emsmybatisliquibase.dto.EmployeeDetailsDto;
import com.learning.emsmybatisliquibase.dto.FeedbackResponseDto;
import com.learning.emsmybatisliquibase.entity.Feedback;
import com.learning.emsmybatisliquibase.entity.enums.FeedbackType;
import com.learning.emsmybatisliquibase.service.FeedbackService;
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
class FeedbackControllerTest {

    MockMvc mockMvc;

    @InjectMocks
    FeedbackController feedbackController;

    @Mock
    FeedbackService feedbackService;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(feedbackController).build();
    }

    @Test
    void testGetById() throws Exception {
        var uuid = UUID.randomUUID();
        var feedback = new Feedback();
        feedback.setUuid(uuid);

        when(feedbackService.getById(uuid)).thenReturn(feedback);

        mockMvc.perform(get("/api/feedback/getById/{uuid}", uuid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(uuid.toString()));

        verify(feedbackService, times(1)).getById(uuid);
    }

    @Test
    void testGetSendFeedback() throws Exception {
        var employee = new EmployeeDetailsDto();
        employee.setUuid(UUID.randomUUID());
        var feedback1 = new FeedbackResponseDto();
        feedback1.setUuid(UUID.randomUUID());
        feedback1.setSender(employee);
        feedback1.setType(FeedbackType.SEND);
        List<FeedbackResponseDto> feedbacks = List.of(feedback1);

        when(feedbackService.getFeedback(employee.getUuid(), FeedbackType.SEND)).thenReturn(feedbacks);

        mockMvc.perform(get("/api/feedback/getFeedback/{employeeId}?feedbackType={type}", employee.getUuid(), FeedbackType.SEND)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(feedbackService, times(1)).getFeedback(employee.getUuid(), FeedbackType.SEND);
    }

    @Test
    void testGetRequestFeedback() throws Exception {
        var employee = new EmployeeDetailsDto();
        employee.setUuid(UUID.randomUUID());
        var feedback1 = new FeedbackResponseDto();
        feedback1.setUuid(UUID.randomUUID());
        feedback1.setSender(employee);
        feedback1.setType(FeedbackType.REQUEST);
        List<FeedbackResponseDto> feedbacks = List.of(feedback1);

        when(feedbackService.getFeedback(employee.getUuid(), FeedbackType.REQUEST)).thenReturn(feedbacks);

        mockMvc.perform(get("/api/feedback/getFeedback/{employeeId}?feedbackType={type}", employee.getUuid(), FeedbackType.REQUEST)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(feedbackService, times(1)).getFeedback(employee.getUuid(), FeedbackType.REQUEST);
    }

    @Test
    void testGetRespondFeedback() throws Exception {
        var employee = new EmployeeDetailsDto();
        employee.setUuid(UUID.randomUUID());
        var feedback1 = new FeedbackResponseDto();
        feedback1.setUuid(UUID.randomUUID());
        feedback1.setSender(employee);
        feedback1.setType(FeedbackType.RESPOND);
        List<FeedbackResponseDto> feedbacks = List.of(feedback1);

        when(feedbackService.getFeedback(employee.getUuid(), FeedbackType.RESPOND)).thenReturn(feedbacks);

        mockMvc.perform(get("/api/feedback/getFeedback/{employeeId}?feedbackType={type}", employee.getUuid(), FeedbackType.RESPOND)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(feedbackService, times(1)).getFeedback(employee.getUuid(), FeedbackType.RESPOND);
    }

    @Test
    void testSend() throws Exception {
        var feedback = new Feedback();
        feedback.setUuid(UUID.randomUUID());

        when(feedbackService.send(feedback)).thenReturn(feedback);

        mockMvc.perform(post("/api/feedback/send")
                        .content(objectMapper.writeValueAsString(feedback))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uuid").value(feedback.getUuid().toString()));

        verify(feedbackService, times(1)).send(feedback);
    }

    @Test
    void testUpdate() throws Exception {
        var uuid = UUID.randomUUID();
        var feedback = new Feedback();
        feedback.setUuid(uuid);

        when(feedbackService.update(uuid, feedback)).thenReturn(feedback);

        mockMvc.perform(put("/api/feedback/update/{feedbackId}", uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedback))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());

        verify(feedbackService, times(1)).update(uuid, feedback);
    }

    @Test
    void testDelete() throws Exception {
        var uuid = UUID.randomUUID();

        doNothing().when(feedbackService).delete(uuid);

        mockMvc.perform(delete("/api/feedback/delete/{feedbackId}", uuid))
                .andExpect(status().isNoContent());

        verify(feedbackService, times(1)).delete(uuid);
    }

}