package com.firstclub.membership.service;

import com.firstclub.membership.dto.TierEvaluationResponse;
import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.entity.Subscription;
import com.firstclub.membership.entity.TierEligibilityRule;
import com.firstclub.membership.entity.TierEvaluationHistory;
import com.firstclub.membership.enums.SubscriptionStatus;
import com.firstclub.membership.provider.OrderMetricsProvider;
import com.firstclub.membership.repository.SubscriptionRepository;
import com.firstclub.membership.repository.TierEligibilityRuleRepository;
import com.firstclub.membership.repository.TierEvaluationHistoryRepository;
import com.firstclub.membership.repository.MembershipTierRepository;
import com.firstclub.membership.strategy.eligibility.EligibilityRuleEvaluator;
import com.firstclub.membership.strategy.eligibility.RuleEvaluatorFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TierEvaluationServiceImpl implements TierEvaluationService {

    private final SubscriptionRepository subscriptionRepository;
    private final MembershipTierRepository tierRepository;
    private final TierEligibilityRuleRepository ruleRepository;
    private final TierEvaluationHistoryRepository historyRepository;
    private final RuleEvaluatorFactory ruleEvaluatorFactory;
    private final OrderMetricsProvider orderMetricsProvider;

    public TierEvaluationServiceImpl(SubscriptionRepository subscriptionRepository,
                                     MembershipTierRepository tierRepository,
                                     TierEligibilityRuleRepository ruleRepository,
                                     TierEvaluationHistoryRepository historyRepository,
                                     RuleEvaluatorFactory ruleEvaluatorFactory,
                                     OrderMetricsProvider orderMetricsProvider) {
        this.subscriptionRepository = subscriptionRepository;
        this.tierRepository = tierRepository;
        this.ruleRepository = ruleRepository;
        this.historyRepository = historyRepository;
        this.ruleEvaluatorFactory = ruleEvaluatorFactory;
        this.orderMetricsProvider = orderMetricsProvider;
    }

    @Override
    @Transactional
    public TierEvaluationResponse evaluateTier(Long userId) {
        Subscription subscription = subscriptionRepository
                .findTopByUserIdAndStatusOrderByCreatedAtDesc(userId, SubscriptionStatus.ACTIVE)
                .orElse(null);

        String currentTierName = null;
        if (subscription != null && subscription.getTier() != null) {
            currentTierName = subscription.getTier().getName();
        }

        List<MembershipTier> allTiers = new ArrayList<>(tierRepository.findAll());
        allTiers.sort((a, b) -> b.getPriority().compareTo(a.getPriority()));

        MembershipTier highestEligibleTier = null;

        for (MembershipTier tier : allTiers) {
            List<TierEligibilityRule> rules = ruleRepository.findByTierIdAndActiveTrue(tier.getId());

            if (rules.isEmpty()) {
                if (highestEligibleTier == null) {
                    highestEligibleTier = tier;
                }
                continue;
            }

            boolean allRulesPass = true;
            for (TierEligibilityRule rule : rules) {
                EligibilityRuleEvaluator evaluator = ruleEvaluatorFactory.getEvaluator(rule.getRuleType());
                boolean passes = evaluator.evaluate(userId, rule, orderMetricsProvider);
                if (!passes) {
                    allRulesPass = false;
                    break;
                }
            }

            if (allRulesPass) {
                if (highestEligibleTier == null || tier.getPriority() > highestEligibleTier.getPriority()) {
                    highestEligibleTier = tier;
                }
            }
        }

        String eligibleTierName = highestEligibleTier != null ? highestEligibleTier.getName() : currentTierName;
        String decision = determineDecision(currentTierName, eligibleTierName);

        if (highestEligibleTier != null && subscription != null && subscription.getTier() != null
                && !highestEligibleTier.getName().equals(subscription.getTier().getName())) {
            TierEvaluationHistory history = TierEvaluationHistory.builder()
                    .userId(userId)
                    .oldTier(subscription.getTier().getName())
                    .newTier(highestEligibleTier.getName())
                    .reason("RULE_EVALUATION")
                    .evaluatedAt(LocalDateTime.now())
                    .build();
            historyRepository.save(history);
        }

        return TierEvaluationResponse.builder()
                .currentTier(currentTierName)
                .eligibleTier(eligibleTierName)
                .decision(decision)
                .evaluatedAt(LocalDateTime.now())
                .build();
    }

    @Override
    public List<TierEvaluationHistory> getEvaluationHistory(Long userId) {
        return historyRepository.findByUserIdOrderByEvaluatedAtDesc(userId);
    }

    @Override
    @Transactional
    public void evaluateAllActiveSubscriptions() {
        List<Subscription> activeSubscriptions = subscriptionRepository.findAll().stream()
                .filter(s -> s.getStatus() == SubscriptionStatus.ACTIVE)
                .toList();

        for (Subscription subscription : activeSubscriptions) {
            try {
                evaluateTier(subscription.getUserId());
            } catch (Exception e) {
                // Log and continue with next subscription
            }
        }
    }

    private String determineDecision(String currentTier, String eligibleTier) {
        if (currentTier == null) {
            return "PENDING";
        }
        if (eligibleTier == null) {
            return "MAINTAIN";
        }
        if (currentTier.equals(eligibleTier)) {
            return "MAINTAIN";
        }

        MembershipTier current = tierRepository.findAll().stream()
                .filter(t -> t.getName().equals(currentTier))
                .findFirst().orElse(null);
        MembershipTier eligible = tierRepository.findAll().stream()
                .filter(t -> t.getName().equals(eligibleTier))
                .findFirst().orElse(null);

        if (current == null || eligible == null) {
            return "PENDING";
        }

        if (eligible.getPriority() > current.getPriority()) {
            return "UPGRADE";
        } else if (eligible.getPriority() < current.getPriority()) {
            return "DOWNGRADE";
        }
        return "MAINTAIN";
    }
}