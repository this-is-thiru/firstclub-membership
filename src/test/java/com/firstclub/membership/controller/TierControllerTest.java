package com.firstclub.membership.controller;

import com.firstclub.membership.dto.TierSummary;
import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.mapper.TierMapper;
import com.firstclub.membership.repository.MembershipTierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TierControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MembershipTierRepository tierRepository;

    @Mock
    private TierMapper tierMapper;

    private TierController tierController;

    @BeforeEach
    void setUp() {
        tierController = new TierController(tierRepository, tierMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(tierController).build();
    }

    @Test
    void getActiveTiers_returnsActiveTiers() throws Exception {
        MembershipTier silverTier = MembershipTier.builder()
                .id(1L)
                .name("SILVER")
                .priority(1)
                .active(true)
                .build();

        TierSummary tierSummary = TierSummary.builder()
                .id(1L)
                .name("SILVER")
                .priority(1)
                .build();

        when(tierRepository.findAll()).thenReturn(List.of(silverTier));
        when(tierMapper.toSummary(silverTier)).thenReturn(tierSummary);

        mockMvc.perform(get("/api/v1/tiers").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("SILVER"))
                .andExpect(jsonPath("$[0].priority").value(1));
    }

    @Test
    void getActiveTiers_filtersInactiveTiers() throws Exception {
        MembershipTier silverTier = MembershipTier.builder()
                .id(1L)
                .name("SILVER")
                .priority(1)
                .active(true)
                .build();

        MembershipTier inactiveTier = MembershipTier.builder()
                .id(99L)
                .name("INACTIVE")
                .priority(0)
                .active(false)
                .build();

        TierSummary tierSummary = TierSummary.builder()
                .id(1L)
                .name("SILVER")
                .priority(1)
                .build();

        when(tierRepository.findAll()).thenReturn(List.of(silverTier, inactiveTier));
        when(tierMapper.toSummary(silverTier)).thenReturn(tierSummary);

        mockMvc.perform(get("/api/v1/tiers").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("SILVER"));
    }
}