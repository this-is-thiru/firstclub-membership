package com.firstclub.membership.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstclub.membership.dto.*;
import com.firstclub.membership.enums.SubscriptionStatus;
import com.firstclub.membership.exception.GlobalExceptionHandler;
import com.firstclub.membership.exception.PlanNotFoundException;
import com.firstclub.membership.exception.SubscriptionNotFoundException;
import com.firstclub.membership.service.SubscriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SubscriptionService subscriptionService;

    private SubscriptionController subscriptionController;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        subscriptionController = new SubscriptionController(subscriptionService);
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders.standaloneSetup(subscriptionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void subscribe_validRequest_returns201() throws Exception {
        SubscribeRequest request = new SubscribeRequest(1L, 1L, 1L);
        SubscriptionResponse response = SubscriptionResponse.builder()
                .id(1L)
                .userId(1L)
                .plan(PlanSummary.builder().id(1L).name("MONTHLY").durationDays(30).price(BigDecimal.valueOf(299)).build())
                .tier(TierSummary.builder().id(1L).name("SILVER").priority(1).build())
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusDays(30))
                .autoRenew(false)
                .version(0L)
                .build();

        when(subscriptionService.subscribe(any(SubscribeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void subscribe_invalidRequest_returns400() throws Exception {
        String invalidJson = "{\"userId\":null,\"planId\":1,\"tierId\":1}";

        mockMvc.perform(post("/api/v1/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void subscribe_planNotFound_returns404() throws Exception {
        SubscribeRequest request = new SubscribeRequest(1L, 999L, 1L);
        when(subscriptionService.subscribe(any(SubscribeRequest.class)))
                .thenThrow(new PlanNotFoundException("Plan not found"));

        mockMvc.perform(post("/api/v1/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCurrentSubscription_exists_returns200() throws Exception {
        SubscriptionResponse response = SubscriptionResponse.builder()
                .id(1L)
                .userId(1L)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusDays(30))
                .build();

        when(subscriptionService.getCurrentSubscription(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/subscriptions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void getCurrentSubscription_notFound_returns404() throws Exception {
        when(subscriptionService.getCurrentSubscription(999L))
                .thenThrow(new SubscriptionNotFoundException("Subscription not found"));

        mockMvc.perform(get("/api/v1/subscriptions/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void upgradeTier_validRequest_returns200() throws Exception {
        UpgradeTierRequest request = new UpgradeTierRequest(2L);
        SubscriptionResponse response = SubscriptionResponse.builder()
                .id(1L)
                .userId(1L)
                .tier(TierSummary.builder().id(2L).name("GOLD").priority(2).build())
                .status(SubscriptionStatus.ACTIVE)
                .version(1L)
                .build();

        when(subscriptionService.upgradeTier(eq(1L), eq(2L))).thenReturn(response);

        mockMvc.perform(put("/api/v1/subscriptions/1/upgrade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tier.name").value("GOLD"));
    }

    @Test
    void downgradeTier_validRequest_returns200() throws Exception {
        DowngradeTierRequest request = new DowngradeTierRequest(1L);
        SubscriptionResponse response = SubscriptionResponse.builder()
                .id(1L)
                .userId(1L)
                .tier(TierSummary.builder().id(1L).name("SILVER").priority(1).build())
                .status(SubscriptionStatus.ACTIVE)
                .version(1L)
                .build();

        when(subscriptionService.downgradeTier(eq(1L), eq(1L))).thenReturn(response);

        mockMvc.perform(put("/api/v1/subscriptions/1/downgrade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tier.name").value("SILVER"));
    }

    @Test
    void cancelSubscription_validRequest_returns204() throws Exception {
        doNothing().when(subscriptionService).cancelSubscription(1L);

        mockMvc.perform(put("/api/v1/subscriptions/1/cancel"))
                .andExpect(status().isNoContent());

        verify(subscriptionService).cancelSubscription(1L);
    }

    @Test
    void cancelSubscription_notFound_returns404() throws Exception {
        doThrow(new SubscriptionNotFoundException("Subscription not found"))
                .when(subscriptionService).cancelSubscription(999L);

        mockMvc.perform(put("/api/v1/subscriptions/999/cancel"))
                .andExpect(status().isNotFound());
    }
}