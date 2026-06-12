package com.firstclub.membership.dto;

import com.firstclub.membership.enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionResponse {
    private Long id;
    private Long userId;
    private PlanSummary plan;
    private TierSummary tier;
    private SubscriptionStatus status;
    private LocalDate startDate;
    private LocalDate expiryDate;
    private Boolean autoRenew;
    private List<BenefitResult> benefits;
    private Long version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}