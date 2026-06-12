package com.firstclub.membership.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TierEvaluationResponse {
    private String currentTier;
    private String eligibleTier;
    private String decision;
    private LocalDateTime evaluatedAt;
}