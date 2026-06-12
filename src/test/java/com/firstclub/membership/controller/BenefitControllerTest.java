package com.firstclub.membership.controller;

import com.firstclub.membership.dto.BenefitResult;
import com.firstclub.membership.enums.BenefitType;
import com.firstclub.membership.exception.GlobalExceptionHandler;
import com.firstclub.membership.exception.SubscriptionNotFoundException;
import com.firstclub.membership.service.BenefitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BenefitControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BenefitService benefitService;

    private BenefitController benefitController;

    @BeforeEach
    void setUp() {
        benefitController = new BenefitController(benefitService);
        mockMvc = MockMvcBuilders.standaloneSetup(benefitController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getEffectiveBenefits_success_returnsBenefits() throws Exception {
        List<BenefitResult> benefits = List.of(
                BenefitResult.builder()
                        .type(BenefitType.FREE_DELIVERY)
                        .name("Free Delivery")
                        .description("Free delivery on all orders")
                        .meta(Map.of())
                        .build(),
                BenefitResult.builder()
                        .type(BenefitType.EXTRA_DISCOUNT)
                        .name("Extra Discount")
                        .description("Additional discount")
                        .meta(Map.of("discount_percentage", "10"))
                        .build()
        );

        when(benefitService.getEffectiveBenefits(1L)).thenReturn(benefits);

        mockMvc.perform(get("/api/v1/users/1/benefits")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].type").value("FREE_DELIVERY"))
                .andExpect(jsonPath("$[0].name").value("Free Delivery"))
                .andExpect(jsonPath("$[1].type").value("EXTRA_DISCOUNT"));
    }

    @Test
    void getEffectiveBenefits_noBenefits_returnsEmptyList() throws Exception {
        when(benefitService.getEffectiveBenefits(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/users/1/benefits")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getEffectiveBenefits_subscriptionNotFound_returns404() throws Exception {
        when(benefitService.getEffectiveBenefits(999L))
                .thenThrow(new SubscriptionNotFoundException("Subscription not found"));

        mockMvc.perform(get("/api/v1/users/999/benefits")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}