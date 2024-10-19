package com.learning.emsmybatisliquibase.controller;

import com.learning.emsmybatisliquibase.entity.Feedback;
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

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FeedbackControllerTest {

    MockMvc mockMvc;

    @InjectMocks
    FeedbackController feedbackController;

    @Mock
    FeedbackService feedbackService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(feedbackController).build();
    }

    @Test
    void testGetById() throws Exception {
        UUID uuid = UUID.randomUUID();
        Feedback feedback = new Feedback();
        feedback.setUuid(uuid);

        when(feedbackService.getById(uuid)).thenReturn(feedback);

        mockMvc.perform(get("/api/feedback/getById/{uuid}", uuid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(uuid.toString()));

        verify(feedbackService, times(1)).getById(uuid);
    }

}