package com.firstclub.membership.strategy.benefit;

import com.firstclub.membership.dto.BenefitResult;
import com.firstclub.membership.entity.MembershipBenefit;
import com.firstclub.membership.enums.BenefitType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ExclusiveDealsBenefitStrategy implements BenefitStrategy {
    @Override
    public BenefitType getType() {
        return BenefitType.EXCLUSIVE_DEALS;
    }

    @Override
    public BenefitResult apply(MembershipBenefit benefit, Map<String, String> config) {
        return BenefitResult.builder()
                .type(BenefitType.EXCLUSIVE_DEALS)
                .name(benefit.getName())
                .description(benefit.getDescription())
                .meta(config)
                .build();
    }
}