package com.firstclub.membership.controller;

import com.firstclub.membership.dto.TierEvaluationResponse;
import com.firstclub.membership.exception.GlobalExceptionHandler;
import com.firstclub.membership.exception.SubscriptionNotFoundException;
import com.firstclub.membership.service.TierEvaluationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TierEvaluationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TierEvaluationService tierEvaluationService;

    private TierEvaluationController tierEvaluationController;

    @BeforeEach
    void setUp() {
        tierEvaluationController = new TierEvaluationController(tierEvaluationService);
        mockMvc = MockMvcBuilders.standaloneSetup(tierEvaluationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void evaluateTier_eligibleForUpgrade_returnsUpgradeDecision() throws Exception {
        TierEvaluationResponse response = TierEvaluationResponse.builder()
                .currentTier("SILVER")
                .eligibleTier("GOLD")
                .decision("UPGRADE")
                .evaluatedAt(LocalDateTime.now())
                .build();

        when(tierEvaluationService.evaluateTier(1L)).thenReturn(response);

        mockMvc.perform(post("/api/v1/tiers/evaluate/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentTier").value("SILVER"))
                .andExpect(jsonPath("$.eligibleTier").value("GOLD"))
                .andExpect(jsonPath("$.decision").value("UPGRADE"));
    }

    @Test
    void evaluateTier_maintainsCurrentTier_returnsMaintainDecision() throws Exception {
        TierEvaluationResponse response = TierEvaluationResponse.builder()
                .currentTier("GOLD")
                .eligibleTier("GOLD")
                .decision("MAINTAIN")
                .evaluatedAt(LocalDateTime.now())
                .build();

        when(tierEvaluationService.evaluateTier(1L)).thenReturn(response);

        mockMvc.perform(post("/api/v1/tiers/evaluate/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.decision").value("MAINTAIN"));
    }

    @Test
    void evaluateTier_subscriptionNotFound_returns404() throws Exception {
        when(tierEvaluationService.evaluateTier(999L))
                .thenThrow(new SubscriptionNotFoundException("Subscription not found"));

        mockMvc.perform(post("/api/v1/tiers/evaluate/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}