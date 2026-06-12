package com.firstclub.membership.strategy.eligibility;

import com.firstclub.membership.entity.TierEligibilityRule;
import com.firstclub.membership.enums.RuleOperator;
import com.firstclub.membership.enums.RuleType;
import com.firstclub.membership.provider.OrderMetricsProvider;
import org.springframework.stereotype.Component;

@Component
public class OrderCountRuleEvaluator implements EligibilityRuleEvaluator {
    @Override
    public boolean supports(RuleType ruleType) {
        return ruleType == RuleType.ORDER_COUNT;
    }

    @Override
    public boolean evaluate(Long userId, TierEligibilityRule rule, OrderMetricsProvider metrics) {
        int actual = metrics.getMonthlyOrderCount(userId);
        int threshold = Integer.parseInt(rule.getValue());
        RuleOperator op = rule.getOperator();
        return switch (op) {
            case GT -> actual > threshold;
            case GTE -> actual >= threshold;
            case EQ -> actual == threshold;
            case LT -> actual < threshold;
            case LTE -> actual <= threshold;
        };
    }
}