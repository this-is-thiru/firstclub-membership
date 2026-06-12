package com.firstclub.membership.service;

import com.firstclub.membership.dto.BenefitResult;
import com.firstclub.membership.entity.MembershipBenefit;
import com.firstclub.membership.entity.Subscription;
import com.firstclub.membership.entity.TierBenefitMapping;
import com.firstclub.membership.enums.SubscriptionStatus;
import com.firstclub.membership.repository.SubscriptionRepository;
import com.firstclub.membership.repository.TierBenefitMappingRepository;
import com.firstclub.membership.strategy.benefit.BenefitStrategy;
import com.firstclub.membership.strategy.benefit.BenefitStrategyFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BenefitServiceImpl implements BenefitService {

    private final SubscriptionRepository subscriptionRepository;
    private final TierBenefitMappingRepository tierBenefitMappingRepository;
    private final BenefitStrategyFactory benefitStrategyFactory;

    public BenefitServiceImpl(SubscriptionRepository subscriptionRepository,
                              TierBenefitMappingRepository tierBenefitMappingRepository,
                              BenefitStrategyFactory benefitStrategyFactory) {
        this.subscriptionRepository = subscriptionRepository;
        this.tierBenefitMappingRepository = tierBenefitMappingRepository;
        this.benefitStrategyFactory = benefitStrategyFactory;
    }

    @Override
    public List<BenefitResult> getEffectiveBenefits(Long userId) {
        Subscription subscription = subscriptionRepository
                .findTopByUserIdAndStatusOrderByCreatedAtDesc(userId, SubscriptionStatus.ACTIVE)
                .orElse(null);

        if (subscription == null) {
            return Collections.emptyList();
        }

        List<TierBenefitMapping> mappings = tierBenefitMappingRepository
                .findByTierId(subscription.getTier().getId());

        List<BenefitResult> results = new ArrayList<>();

        for (TierBenefitMapping mapping : mappings) {
            MembershipBenefit benefit = mapping.getBenefit();
            Map<String, String> configMap = buildConfigMap(benefit);
            BenefitStrategy strategy = benefitStrategyFactory.getStrategy(benefit.getType());
            BenefitResult result = strategy.apply(benefit, configMap);
            results.add(result);
        }

        return results;
    }

    private Map<String, String> buildConfigMap(MembershipBenefit benefit) {
        Map<String, String> configMap = new HashMap<>();
        if (benefit.getConfigs() != null) {
            for (var config : benefit.getConfigs()) {
                configMap.put(config.getConfigKey(), config.getConfigValue());
            }
        }
        return configMap;
    }
}