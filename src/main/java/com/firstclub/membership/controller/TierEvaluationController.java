package com.firstclub.membership.controller;

import com.firstclub.membership.dto.TierEvaluationResponse;
import com.firstclub.membership.service.TierEvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/tiers/evaluate")
@Tag(name = "Tier Evaluation", description = "Tier eligibility evaluation")
public class TierEvaluationController {

    private final TierEvaluationService tierEvaluationService;

    public TierEvaluationController(TierEvaluationService tierEvaluationService) {
        this.tierEvaluationService = tierEvaluationService;
    }

    @PostMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Evaluate tier eligibility for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evaluation completed successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(name = "Upgrade Decision",
                                            value = """
                                                    {
                                                      "currentTier": "SILVER",
                                                      "eligibleTier": "GOLD",
                                                      "decision": "UPGRADE",
                                                      "evaluatedAt": "2026-06-10T10:30:00"
                                                    }
                                                    """),
                                    @ExampleObject(name = "Maintain Decision",
                                            value = """
                                                    {
                                                      "currentTier": "GOLD",
                                                      "eligibleTier": "GOLD",
                                                      "decision": "MAINTAIN",
                                                      "evaluatedAt": "2026-06-10T10:30:00"
                                                    }
                                                    """)
                            })),
            @ApiResponse(responseCode = "404", description = "User subscription not found")
    })
    public ResponseEntity<TierEvaluationResponse> evaluateTier(@PathVariable Long userId) {
        return ResponseEntity.ok(tierEvaluationService.evaluateTier(userId));
    }
}