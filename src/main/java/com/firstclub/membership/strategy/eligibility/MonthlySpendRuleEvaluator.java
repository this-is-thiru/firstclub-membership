package com.firstclub.membership.strategy.eligibility;

import com.firstclub.membership.entity.TierEligibilityRule;
import com.firstclub.membership.enums.RuleOperator;
import com.firstclub.membership.enums.RuleType;
import com.firstclub.membership.provider.OrderMetricsProvider;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MonthlySpendRuleEvaluator implements EligibilityRuleEvaluator {
    @Override
    public boolean supports(RuleType ruleType) {
        return ruleType == RuleType.MONTHLY_SPEND;
    }

    @Override
    public boolean evaluate(Long userId, TierEligibilityRule rule, OrderMetricsProvider metrics) {
        BigDecimal actual = metrics.getMonthlySpend(userId);
        BigDecimal threshold = new BigDecimal(rule.getValue());
        RuleOperator op = rule.getOperator();
        return switch (op) {
            case GT -> actual.compareTo(threshold) > 0;
            case GTE -> actual.compareTo(threshold) >= 0;
            case EQ -> actual.compareTo(threshold) == 0;
            case LT -> actual.compareTo(threshold) < 0;
            case LTE -> actual.compareTo(threshold) <= 0;
        };
    }
}