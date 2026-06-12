package com.firstclub.membership.controller;

import com.firstclub.membership.dto.TierSummary;
import com.firstclub.membership.mapper.TierMapper;
import com.firstclub.membership.repository.MembershipTierRepository;
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
@RequestMapping("/api/v1/tiers")
@Tag(name = "Tiers", description = "Membership tier management")
public class TierController {

    private final MembershipTierRepository tierRepository;
    private final TierMapper tierMapper;

    public TierController(MembershipTierRepository tierRepository, TierMapper tierMapper) {
        this.tierRepository = tierRepository;
        this.tierMapper = tierMapper;
    }

    @GetMapping
    @Operation(summary = "Get all active tiers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved active tiers")
    })
    public ResponseEntity<List<TierSummary>> getActiveTiers() {
        return ResponseEntity.ok(
                tierRepository.findAll().stream()
                        .filter(tier -> Boolean.TRUE.equals(tier.getActive()))
                        .map(tierMapper::toSummary)
                        .collect(Collectors.toList())
        );
    }
}