package com.firstclub.membership.strategy.eligibility;

import com.firstclub.membership.entity.TierEligibilityRule;
import com.firstclub.membership.enums.RuleOperator;
import com.firstclub.membership.enums.RuleType;
import com.firstclub.membership.provider.OrderMetricsProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RuleEvaluatorTest {

    @Mock
    private OrderMetricsProvider metricsProvider;

    private RuleEvaluatorFactory factory;

    @BeforeEach
    void setUp() {
        List<EligibilityRuleEvaluator> evaluators = List.of(
                new OrderCountRuleEvaluator(),
                new MonthlySpendRuleEvaluator(),
                new CohortRuleEvaluator()
        );
        factory = new RuleEvaluatorFactory(evaluators);
    }

    // OrderCountRuleEvaluator tests
    @Test
    void orderCountEvaluator_supportsCorrectType() {
        OrderCountRuleEvaluator evaluator = new OrderCountRuleEvaluator();
        assertTrue(evaluator.supports(RuleType.ORDER_COUNT));
        assertFalse(evaluator.supports(RuleType.MONTHLY_SPEND));
        assertFalse(evaluator.supports(RuleType.COHORT));
    }

    @Test
    void orderCountEvaluator_GT_returnsTrueWhenActualGreater() {
        when(metricsProvider.getMonthlyOrderCount(1L)).thenReturn(15);
        TierEligibilityRule rule = TierEligibilityRule.builder()
                .ruleType(RuleType.ORDER_COUNT)
                .operator(RuleOperator.GT)
                .value("10")
                .build();
        OrderCountRuleEvaluator evaluator = new OrderCountRuleEvaluator();
        assertTrue(evaluator.evaluate(1L, rule, metricsProvider));
    }

    @Test
    void orderCountEvaluator_GT_returnsFalseWhenActualEqual() {
        when(metricsProvider.getMonthlyOrderCount(1L)).thenReturn(10);
        TierEligibilityRule rule = TierEligibilityRule.builder()
                .ruleType(RuleType.ORDER_COUNT)
                .operator(RuleOperator.GT)
                .value("10")
                .build();
        OrderCountRuleEvaluator evaluator = new OrderCountRuleEvaluator();
        assertFalse(evaluator.evaluate(1L, rule, metricsProvider));
    }

    @Test
    void orderCountEvaluator_GTE_returnsTrueWhenEqual() {
        when(metricsProvider.getMonthlyOrderCount(1L)).thenReturn(10);
        TierEligibilityRule rule = TierEligibilityRule.builder()
                .ruleType(RuleType.ORDER_COUNT)
                .operator(RuleOperator.GTE)
                .value("10")
                .build();
        OrderCountRuleEvaluator evaluator = new OrderCountRuleEvaluator();
        assertTrue(evaluator.evaluate(1L, rule, metricsProvider));
    }

    @Test
    void orderCountEvaluator_EQ_returnsTrue() {
        when(metricsProvider.getMonthlyOrderCount(1L)).thenReturn(5);
        TierEligibilityRule rule = TierEligibilityRule.builder()
                .ruleType(RuleType.ORDER_COUNT)
                .operator(RuleOperator.EQ)
                .value("5")
                .build();
        OrderCountRuleEvaluator evaluator = new OrderCountRuleEvaluator();
        assertTrue(evaluator.evaluate(1L, rule, metricsProvider));
    }

    @Test
    void orderCountEvaluator_LT_returnsTrueWhenActualLess() {
        when(metricsProvider.getMonthlyOrderCount(1L)).thenReturn(3);
        TierEligibilityRule rule = TierEligibilityRule.builder()
                .ruleType(RuleType.ORDER_COUNT)
                .operator(RuleOperator.LT)
                .value("5")
                .build();
        OrderCountRuleEvaluator evaluator = new OrderCountRuleEvaluator();
        assertTrue(evaluator.evaluate(1L, rule, metricsProvider));
    }

    @Test
    void orderCountEvaluator_LTE_returnsTrueWhenEqual() {
        when(metricsProvider.getMonthlyOrderCount(1L)).thenReturn(5);
        TierEligibilityRule rule = TierEligibilityRule.builder()
                .ruleType(RuleType.ORDER_COUNT)
                .operator(RuleOperator.LTE)
                .value("5")
                .build();
        OrderCountRuleEvaluator evaluator = new OrderCountRuleEvaluator();
        assertTrue(evaluator.evaluate(1L, rule, metricsProvider));
    }

    // MonthlySpendRuleEvaluator tests
    @Test
    void monthlySpendEvaluator_supportsCorrectType() {
        MonthlySpendRuleEvaluator evaluator = new MonthlySpendRuleEvaluator();
        assertTrue(evaluator.supports(RuleType.MONTHLY_SPEND));
        assertFalse(evaluator.supports(RuleType.ORDER_COUNT));
        assertFalse(evaluator.supports(RuleType.COHORT));
    }

    @Test
    void monthlySpendEvaluator_GT_returnsTrueWhenActualGreater() {
        when(metricsProvider.getMonthlySpend(1L)).thenReturn(new BigDecimal("6000"));
        TierEligibilityRule rule = TierEligibilityRule.builder()
                .ruleType(RuleType.MONTHLY_SPEND)
                .operator(RuleOperator.GT)
                .value("5000")
                .build();
        MonthlySpendRuleEvaluator evaluator = new MonthlySpendRuleEvaluator();
        assertTrue(evaluator.evaluate(1L, rule, metricsProvider));
    }

    @Test
    void monthlySpendEvaluator_GT_returnsFalseWhenEqual() {
        when(metricsProvider.getMonthlySpend(1L)).thenReturn(new BigDecimal("5000"));
        TierEligibilityRule rule = TierEligibilityRule.builder()
                .ruleType(RuleType.MONTHLY_SPEND)
                .operator(RuleOperator.GT)
                .value("5000")
                .build();
        MonthlySpendRuleEvaluator evaluator = new MonthlySpendRuleEvaluator();
        assertFalse(evaluator.evaluate(1L, rule, metricsProvider));
    }

    @Test
    void monthlySpendEvaluator_GTE_returnsTrueWhenEqual() {
        when(metricsProvider.getMonthlySpend(1L)).thenReturn(new BigDecimal("5000"));
        TierEligibilityRule rule = TierEligibilityRule.builder()
                .ruleType(RuleType.MONTHLY_SPEND)
                .operator(RuleOperator.GTE)
                .value("5000")
                .build();
        MonthlySpendRuleEvaluator evaluator = new MonthlySpendRuleEvaluator();
        assertTrue(evaluator.evaluate(1L, rule, metricsProvider));
    }

    @Test
    void monthlySpendEvaluator_EQ_returnsTrue() {
        when(metricsProvider.getMonthlySpend(1L)).thenReturn(new BigDecimal("2500.50"));
        TierEligibilityRule rule = TierEligibilityRule.builder()
                .ruleType(RuleType.MONTHLY_SPEND)
                .operator(RuleOperator.EQ)
                .value("2500.50")
                .build();
        MonthlySpendRuleEvaluator evaluator = new MonthlySpendRuleEvaluator();
        assertTrue(evaluator.evaluate(1L, rule, metricsProvider));
    }

    // CohortRuleEvaluator tests
    @Test
    void cohortEvaluator_supportsCorrectType() {
        CohortRuleEvaluator evaluator = new CohortRuleEvaluator();
        assertTrue(evaluator.supports(RuleType.COHORT));
        assertFalse(evaluator.supports(RuleType.ORDER_COUNT));
        assertFalse(evaluator.supports(RuleType.MONTHLY_SPEND));
    }

    @Test
    void cohortEvaluator_EQ_returnsTrueWhenMatching() {
        when(metricsProvider.getUserCohort(1L)).thenReturn("PREMIUM_USERS");
        TierEligibilityRule rule = TierEligibilityRule.builder()
                .ruleType(RuleType.COHORT)
                .operator(RuleOperator.EQ)
                .value("PREMIUM_USERS")
                .build();
        CohortRuleEvaluator evaluator = new CohortRuleEvaluator();
        assertTrue(evaluator.evaluate(1L, rule, metricsProvider));
    }

    @Test
    void cohortEvaluator_EQ_returnsFalseWhenNotMatching() {
        when(metricsProvider.getUserCohort(1L)).thenReturn("REGULAR_USERS");
        TierEligibilityRule rule = TierEligibilityRule.builder()
                .ruleType(RuleType.COHORT)
                .operator(RuleOperator.EQ)
                .value("PREMIUM_USERS")
                .build();
        CohortRuleEvaluator evaluator = new CohortRuleEvaluator();
        assertFalse(evaluator.evaluate(1L, rule, metricsProvider));
    }

    @Test
    void factory_getEvaluator_returnsCorrectEvaluator() {
        assertInstanceOf(OrderCountRuleEvaluator.class, factory.getEvaluator(RuleType.ORDER_COUNT));
        assertInstanceOf(MonthlySpendRuleEvaluator.class, factory.getEvaluator(RuleType.MONTHLY_SPEND));
        assertInstanceOf(CohortRuleEvaluator.class, factory.getEvaluator(RuleType.COHORT));
    }
}