package com.firstclub.membership.strategy.benefit;

import com.firstclub.membership.enums.BenefitType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BenefitStrategyFactory {
    private final Map<BenefitType, BenefitStrategy> strategies;

    public BenefitStrategyFactory(List<BenefitStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(BenefitStrategy::getType, Function.identity()));
    }

    public BenefitStrategy getStrategy(BenefitType type) {
        BenefitStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy found for benefit type: " + type);
        }
        return strategy;
    }
}