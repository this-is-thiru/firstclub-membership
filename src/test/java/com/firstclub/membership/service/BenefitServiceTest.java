package com.firstclub.membership.service;

import com.firstclub.membership.dto.BenefitResult;
import com.firstclub.membership.entity.BenefitConfig;
import com.firstclub.membership.entity.MembershipBenefit;
import com.firstclub.membership.entity.MembershipPlan;
import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.entity.Subscription;
import com.firstclub.membership.entity.TierBenefitMapping;
import com.firstclub.membership.enums.BenefitType;
import com.firstclub.membership.enums.SubscriptionStatus;
import com.firstclub.membership.repository.SubscriptionRepository;
import com.firstclub.membership.repository.TierBenefitMappingRepository;
import com.firstclub.membership.strategy.benefit.BenefitStrategy;
import com.firstclub.membership.strategy.benefit.BenefitStrategyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BenefitServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private TierBenefitMappingRepository tierBenefitMappingRepository;

    @Mock
    private BenefitStrategyFactory benefitStrategyFactory;

    @Mock
    private BenefitStrategy benefitStrategy;

    private BenefitServiceImpl benefitService;

    @BeforeEach
    void setUp() {
        benefitService = new BenefitServiceImpl(
                subscriptionRepository,
                tierBenefitMappingRepository,
                benefitStrategyFactory
        );
    }

    @Test
    void getEffectiveBenefits_noActiveSubscription_returnsEmptyList() {
        when(subscriptionRepository.findTopByUserIdAndStatusOrderByCreatedAtDesc(1L, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.empty());

        List<BenefitResult> results = benefitService.getEffectiveBenefits(1L);

        assertTrue(results.isEmpty());
    }

    @Test
    void getEffectiveBenefits_withActiveSubscription_returnsBenefits() {
        Long userId = 1L;
        Long tierId = 1L;

        MembershipTier tier = MembershipTier.builder()
                .id(tierId)
                .name("GOLD")
                .priority(2)
                .active(true)
                .build();

        MembershipPlan plan = MembershipPlan.builder()
                .id(1L)
                .name("MONTHLY")
                .durationDays(30)
                .price(BigDecimal.valueOf(299))
                .active(true)
                .build();

        Subscription subscription = Subscription.builder()
                .id(1L)
                .userId(userId)
                .tier(tier)
                .plan(plan)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusDays(30))
                .build();

        BenefitConfig config = BenefitConfig.builder()
                .id(1L)
                .configKey("discount_percentage")
                .configValue("10")
                .build();

        MembershipBenefit benefit = MembershipBenefit.builder()
                .id(1L)
                .type(BenefitType.EXTRA_DISCOUNT)
                .name("Extra Discount")
                .description("Additional discount")
                .active(true)
                .configs(List.of(config))
                .build();

        TierBenefitMapping mapping = TierBenefitMapping.builder()
                .id(1L)
                .tier(tier)
                .benefit(benefit)
                .build();

        BenefitResult expectedResult = BenefitResult.builder()
                .type(BenefitType.EXTRA_DISCOUNT)
                .name("Extra Discount")
                .description("Additional discount")
                .meta(Map.of("discount_percentage", "10"))
                .build();

        when(subscriptionRepository.findTopByUserIdAndStatusOrderByCreatedAtDesc(userId, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(subscription));
        when(tierBenefitMappingRepository.findByTierId(tierId))
                .thenReturn(List.of(mapping));
        when(benefitStrategyFactory.getStrategy(BenefitType.EXTRA_DISCOUNT))
                .thenReturn(benefitStrategy);
        when(benefitStrategy.apply(eq(benefit), any()))
                .thenReturn(expectedResult);

        List<BenefitResult> results = benefitService.getEffectiveBenefits(userId);

        assertEquals(1, results.size());
        assertEquals(BenefitType.EXTRA_DISCOUNT, results.get(0).getType());
        assertEquals("Extra Discount", results.get(0).getName());
    }

    @Test
    void getEffectiveBenefits_multipleBenefits_returnsAllBenefits() {
        Long userId = 1L;
        Long tierId = 1L;

        MembershipTier tier = MembershipTier.builder()
                .id(tierId)
                .name("PLATINUM")
                .priority(3)
                .active(true)
                .build();

        MembershipPlan plan = MembershipPlan.builder()
                .id(1L)
                .name("YEARLY")
                .durationDays(365)
                .price(BigDecimal.valueOf(2499))
                .active(true)
                .build();

        Subscription subscription = Subscription.builder()
                .id(1L)
                .userId(userId)
                .tier(tier)
                .plan(plan)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusDays(365))
                .build();

        MembershipBenefit benefit1 = MembershipBenefit.builder()
                .id(1L)
                .type(BenefitType.FREE_DELIVERY)
                .name("Free Delivery")
                .description("Free delivery on all orders")
                .active(true)
                .configs(Collections.emptyList())
                .build();

        MembershipBenefit benefit2 = MembershipBenefit.builder()
                .id(2L)
                .type(BenefitType.PRIORITY_SUPPORT)
                .name("Priority Support")
                .description("Priority customer support")
                .active(true)
                .configs(Collections.emptyList())
                .build();

        TierBenefitMapping mapping1 = TierBenefitMapping.builder()
                .id(1L)
                .tier(tier)
                .benefit(benefit1)
                .build();

        TierBenefitMapping mapping2 = TierBenefitMapping.builder()
                .id(2L)
                .tier(tier)
                .benefit(benefit2)
                .build();

        BenefitResult result1 = BenefitResult.builder()
                .type(BenefitType.FREE_DELIVERY)
                .name("Free Delivery")
                .build();

        BenefitResult result2 = BenefitResult.builder()
                .type(BenefitType.PRIORITY_SUPPORT)
                .name("Priority Support")
                .build();

        when(subscriptionRepository.findTopByUserIdAndStatusOrderByCreatedAtDesc(userId, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(subscription));
        when(tierBenefitMappingRepository.findByTierId(tierId))
                .thenReturn(List.of(mapping1, mapping2));
        when(benefitStrategyFactory.getStrategy(BenefitType.FREE_DELIVERY)).thenReturn(benefitStrategy);
        when(benefitStrategyFactory.getStrategy(BenefitType.PRIORITY_SUPPORT)).thenReturn(benefitStrategy);
        when(benefitStrategy.apply(eq(benefit1), any())).thenReturn(result1);
        when(benefitStrategy.apply(eq(benefit2), any())).thenReturn(result2);

        List<BenefitResult> results = benefitService.getEffectiveBenefits(userId);

        assertEquals(2, results.size());
    }
}