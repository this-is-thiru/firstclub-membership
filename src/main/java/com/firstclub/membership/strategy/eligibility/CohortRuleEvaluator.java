package com.firstclub.membership.strategy.eligibility;

import com.firstclub.membership.entity.TierEligibilityRule;
import com.firstclub.membership.enums.RuleOperator;
import com.firstclub.membership.enums.RuleType;
import com.firstclub.membership.provider.OrderMetricsProvider;
import org.springframework.stereotype.Component;

@Component
public class CohortRuleEvaluator implements EligibilityRuleEvaluator {
    @Override
    public boolean supports(RuleType ruleType) {
        return ruleType == RuleType.COHORT;
    }

    @Override
    public boolean evaluate(Long userId, TierEligibilityRule rule, OrderMetricsProvider metrics) {
        String actual = metrics.getUserCohort(userId);
        String expected = rule.getValue();
        RuleOperator op = rule.getOperator();
        return switch (op) {
            case EQ -> expected.equals(actual);
            default -> false; // Cohort only supports EQ for now
        };
    }
}