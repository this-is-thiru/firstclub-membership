package com.firstclub.membership.controller;

import com.firstclub.membership.dto.PlanSummary;
import com.firstclub.membership.entity.MembershipPlan;
import com.firstclub.membership.mapper.PlanMapper;
import com.firstclub.membership.repository.MembershipPlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PlanControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MembershipPlanRepository planRepository;

    @Mock
    private PlanMapper planMapper;

    private PlanController planController;

    @BeforeEach
    void setUp() {
        planController = new PlanController(planRepository, planMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(planController).build();
    }

    @Test
    void getActivePlans_returnsActivePlans() throws Exception {
        MembershipPlan monthlyPlan = MembershipPlan.builder()
                .id(1L)
                .name("MONTHLY")
                .durationDays(30)
                .price(BigDecimal.valueOf(299))
                .active(true)
                .build();

        PlanSummary planSummary = PlanSummary.builder()
                .id(1L)
                .name("MONTHLY")
                .durationDays(30)
                .price(BigDecimal.valueOf(299))
                .build();

        when(planRepository.findAll()).thenReturn(List.of(monthlyPlan));
        when(planMapper.toSummary(monthlyPlan)).thenReturn(planSummary);

        mockMvc.perform(get("/api/v1/plans").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("MONTHLY"))
                .andExpect(jsonPath("$[0].durationDays").value(30));
    }

    @Test
    void getActivePlans_filtersInactivePlans() throws Exception {
        MembershipPlan monthlyPlan = MembershipPlan.builder()
                .id(1L)
                .name("MONTHLY")
                .durationDays(30)
                .price(BigDecimal.valueOf(299))
                .active(true)
                .build();

        MembershipPlan inactivePlan = MembershipPlan.builder()
                .id(2L)
                .name("DISCONTINUED")
                .durationDays(30)
                .price(BigDecimal.valueOf(199))
                .active(false)
                .build();

        PlanSummary planSummary = PlanSummary.builder()
                .id(1L)
                .name("MONTHLY")
                .durationDays(30)
                .price(BigDecimal.valueOf(299))
                .build();

        when(planRepository.findAll()).thenReturn(List.of(monthlyPlan, inactivePlan));
        when(planMapper.toSummary(monthlyPlan)).thenReturn(planSummary);

        mockMvc.perform(get("/api/v1/plans").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("MONTHLY"));
    }
}