package com.firstclub.membership.strategy.benefit;

import com.firstclub.membership.dto.BenefitResult;
import com.firstclub.membership.entity.MembershipBenefit;
import com.firstclub.membership.enums.BenefitType;

import java.util.Map;

public interface BenefitStrategy {
    BenefitType getType();
    BenefitResult apply(MembershipBenefit benefit, Map<String, String> config);
}