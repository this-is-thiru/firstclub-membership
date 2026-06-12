package com.firstclub.membership.mapper;

import com.firstclub.membership.dto.TierSummary;
import com.firstclub.membership.entity.MembershipTier;
import org.springframework.stereotype.Component;

@Component
public class TierMapper {
    public TierSummary toSummary(MembershipTier tier) {
        if (tier == null) {
            return null;
        }
        return TierSummary.builder()
                .id(tier.getId())
                .name(tier.getName())
                .priority(tier.getPriority())
                .build();
    }
}