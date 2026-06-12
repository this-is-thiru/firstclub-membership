package com.firstclub.membership.strategy.eligibility;

import com.firstclub.membership.entity.TierEligibilityRule;
import com.firstclub.membership.enums.RuleType;
import com.firstclub.membership.provider.OrderMetricsProvider;

public interface EligibilityRuleEvaluator {
    boolean supports(RuleType ruleType);
    boolean evaluate(Long userId, TierEligibilityRule rule, OrderMetricsProvider metrics);
}