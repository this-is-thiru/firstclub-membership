package com.firstclub.membership.strategy.benefit;

import com.firstclub.membership.dto.BenefitResult;
import com.firstclub.membership.entity.MembershipBenefit;
import com.firstclub.membership.enums.BenefitType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EarlyAccessBenefitStrategy implements BenefitStrategy {
    @Override
    public BenefitType getType() {
        return BenefitType.EARLY_ACCESS;
    }

    @Override
    public BenefitResult apply(MembershipBenefit benefit, Map<String, String> config) {
        return BenefitResult.builder()
                .type(BenefitType.EARLY_ACCESS)
                .name(benefit.getName())
                .description(benefit.getDescription())
                .meta(config)
                .build();
    }
}