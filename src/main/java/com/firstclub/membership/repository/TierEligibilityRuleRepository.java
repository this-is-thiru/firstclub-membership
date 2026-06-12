package com.firstclub.membership.repository;

import com.firstclub.membership.entity.TierEligibilityRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TierEligibilityRuleRepository extends JpaRepository<TierEligibilityRule, Long> {
    List<TierEligibilityRule> findByTierIdAndActiveTrue(Long tierId);
}