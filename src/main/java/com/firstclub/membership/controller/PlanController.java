package com.firstclub.membership.controller;

import com.firstclub.membership.dto.PlanSummary;
import com.firstclub.membership.mapper.PlanMapper;
import com.firstclub.membership.repository.MembershipPlanRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/plans")
@Tag(name = "Plans", description = "Membership plan management")
public class PlanController {

    private final MembershipPlanRepository planRepository;
    private final PlanMapper planMapper;

    public PlanController(MembershipPlanRepository planRepository, PlanMapper planMapper) {
        this.planRepository = planRepository;
        this.planMapper = planMapper;
    }

    @GetMapping
    @Operation(summary = "Get all active plans")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved active plans")
    })
    public ResponseEntity<List<PlanSummary>> getActivePlans() {
        return ResponseEntity.ok(
                planRepository.findAll().stream()
                        .filter(plan -> Boolean.TRUE.equals(plan.getActive()))
                        .map(planMapper::toSummary)
                        .collect(Collectors.toList())
        );
    }
}