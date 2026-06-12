package com.firstclub.membership.strategy.eligibility;

import com.firstclub.membership.enums.RuleType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RuleEvaluatorFactory {
    private final Map<RuleType, EligibilityRuleEvaluator> evaluators;

    public RuleEvaluatorFactory(List<EligibilityRuleEvaluator> evaluatorList) {
        this.evaluators = evaluatorList.stream()
                .collect(Collectors.toMap(e -> {
                    // Support only single RuleType per evaluator for simplicity
                    return e.supports(RuleType.ORDER_COUNT) ? RuleType.ORDER_COUNT :
                           e.supports(RuleType.MONTHLY_SPEND) ? RuleType.MONTHLY_SPEND :
                           e.supports(RuleType.COHORT) ? RuleType.COHORT : null;
                }, Function.identity()));
    }

    public EligibilityRuleEvaluator getEvaluator(RuleType type) {
        EligibilityRuleEvaluator evaluator = evaluators.get(type);
        if (evaluator == null) {
            throw new IllegalArgumentException("No evaluator found for rule type: " + type);
        }
        return evaluator;
    }
}