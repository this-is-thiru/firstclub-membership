package com.firstclub.membership.mapper;

import com.firstclub.membership.dto.PlanSummary;
import com.firstclub.membership.entity.MembershipPlan;
import org.springframework.stereotype.Component;

@Component
public class PlanMapper {
    public PlanSummary toSummary(MembershipPlan plan) {
        if (plan == null) {
            return null;
        }
        return PlanSummary.builder()
                .id(plan.getId())
                .name(plan.getName())
                .durationDays(plan.getDurationDays())
                .price(plan.getPrice())
                .build();
    }
}