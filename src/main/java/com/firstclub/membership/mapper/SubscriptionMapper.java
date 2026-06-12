package com.firstclub.membership.mapper;

import com.firstclub.membership.dto.BenefitResult;
import com.firstclub.membership.dto.SubscriptionResponse;
import com.firstclub.membership.entity.Subscription;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SubscriptionMapper {
    private final PlanMapper planMapper;
    private final TierMapper tierMapper;

    public SubscriptionMapper(PlanMapper planMapper, TierMapper tierMapper) {
        this.planMapper = planMapper;
        this.tierMapper = tierMapper;
    }

    public SubscriptionResponse toResponse(Subscription subscription, List<BenefitResult> benefits) {
        if (subscription == null) {
            return null;
        }
        return SubscriptionResponse.builder()
                .id(subscription.getId())
                .userId(subscription.getUserId())
                .plan(planMapper.toSummary(subscription.getPlan()))
                .tier(tierMapper.toSummary(subscription.getTier()))
                .status(subscription.getStatus())
                .startDate(subscription.getStartDate())
                .expiryDate(subscription.getExpiryDate())
                .autoRenew(subscription.getAutoRenew())
                .benefits(benefits)
                .version(subscription.getVersion())
                .createdAt(subscription.getCreatedAt())
                .updatedAt(subscription.getUpdatedAt())
                .build();
    }
}