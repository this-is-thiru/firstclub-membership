package com.firstclub.membership.service;

import com.firstclub.membership.dto.BenefitResult;
import com.firstclub.membership.dto.SubscribeRequest;
import com.firstclub.membership.dto.SubscriptionResponse;
import com.firstclub.membership.entity.MembershipPlan;
import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.entity.Subscription;
import com.firstclub.membership.enums.BenefitType;
import com.firstclub.membership.enums.SubscriptionStatus;
import com.firstclub.membership.exception.ConcurrentModificationException;
import com.firstclub.membership.exception.InvalidTierTransitionException;
import com.firstclub.membership.exception.SubscriptionNotFoundException;
import com.firstclub.membership.mapper.SubscriptionMapper;
import com.firstclub.membership.repository.MembershipPlanRepository;
import com.firstclub.membership.repository.MembershipTierRepository;
import com.firstclub.membership.repository.SubscriptionRepository;
import jakarta.persistence.OptimisticLockException;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private MembershipPlanRepository planRepository;

    @Mock
    private MembershipTierRepository tierRepository;

    @Mock
    private SubscriptionMapper subscriptionMapper;

    @Mock
    private BenefitService benefitService;

    private SubscriptionServiceImpl subscriptionService;

    private MembershipPlan monthlyPlan;
    private MembershipPlan yearlyPlan;
    private MembershipTier silverTier;
    private MembershipTier goldTier;
    private MembershipTier platinumTier;
    private SubscriptionResponse mockResponse;

    @BeforeEach
    void setUp() {
        subscriptionService = new SubscriptionServiceImpl(
                subscriptionRepository,
                planRepository,
                tierRepository,
                subscriptionMapper,
                benefitService
        );

        monthlyPlan = MembershipPlan.builder()
                .id(1L)
                .name("MONTHLY")
                .durationDays(30)
                .price(BigDecimal.valueOf(299))
                .active(true)
                .build();

        yearlyPlan = MembershipPlan.builder()
                .id(2L)
                .name("YEARLY")
                .durationDays(365)
                .price(BigDecimal.valueOf(2499))
                .active(true)
                .build();

        silverTier = MembershipTier.builder()
                .id(1L)
                .name("SILVER")
                .priority(1)
                .active(true)
                .build();

        goldTier = MembershipTier.builder()
                .id(2L)
                .name("GOLD")
                .priority(2)
                .active(true)
                .build();

        platinumTier = MembershipTier.builder()
                .id(3L)
                .name("PLATINUM")
                .priority(3)
                .active(true)
                .build();

        mockResponse = SubscriptionResponse.builder()
                .id(1L)
                .userId(1L)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusDays(30))
                .autoRenew(false)
                .build();
    }

    @Test
    void subscribe_validRequest_createsSubscription() {
        Long userId = 1L;
        SubscribeRequest req = new SubscribeRequest(userId, 1L, 1L);

        when(planRepository.findById(1L)).thenReturn(Optional.of(monthlyPlan));
        when(tierRepository.findById(1L)).thenReturn(Optional.of(silverTier));
        when(subscriptionRepository.findTopByUserIdAndStatusOrderByCreatedAtDesc(userId, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.empty());
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(inv -> {
            Subscription s = inv.getArgument(0);
            s.setId(1L);
            return s;
        });
        when(benefitService.getEffectiveBenefits(userId)).thenReturn(Collections.emptyList());
        when(subscriptionMapper.toResponse(any(Subscription.class), any())).thenReturn(mockResponse);

        SubscriptionResponse response = subscriptionService.subscribe(req);

        assertNotNull(response);
        assertEquals(SubscriptionStatus.ACTIVE, response.getStatus());
        verify(subscriptionRepository).save(any(Subscription.class));
    }

    @Test
    void subscribe_existingActiveSubscription_throwsException() {
        Long userId = 1L;
        SubscribeRequest req = new SubscribeRequest(userId, 1L, 1L);

        Subscription existingSubscription = Subscription.builder()
                .id(1L)
                .userId(userId)
                .plan(monthlyPlan)
                .tier(silverTier)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusDays(30))
                .build();

        when(planRepository.findById(1L)).thenReturn(Optional.of(monthlyPlan));
        when(tierRepository.findById(1L)).thenReturn(Optional.of(silverTier));
        when(subscriptionRepository.findTopByUserIdAndStatusOrderByCreatedAtDesc(userId, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(existingSubscription));

        assertThrows(com.firstclub.membership.exception.MembershipException.class,
                () -> subscriptionService.subscribe(req));
    }

    @Test
    void getCurrentSubscription_activeSubscriptionFound_returnsSubscription() {
        Long userId = 1L;
        Subscription subscription = Subscription.builder()
                .id(1L)
                .userId(userId)
                .plan(monthlyPlan)
                .tier(silverTier)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusDays(30))
                .build();

        when(subscriptionRepository.findTopByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(Optional.of(subscription));
        when(benefitService.getEffectiveBenefits(userId)).thenReturn(Collections.emptyList());
        when(subscriptionMapper.toResponse(subscription, Collections.emptyList())).thenReturn(mockResponse);

        SubscriptionResponse response = subscriptionService.getCurrentSubscription(userId);

        assertNotNull(response);
        assertEquals(userId, response.getUserId());
    }

    @Test
    void getCurrentSubscription_noActiveSubscription_throwsException() {
        Long userId = 1L;
        when(subscriptionRepository.findTopByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(Optional.empty());

        assertThrows(SubscriptionNotFoundException.class,
                () -> subscriptionService.getCurrentSubscription(userId));
    }

    @Test
    void upgradeTier_validUpgrade_returnsUpdatedSubscription() {
        Long userId = 1L;
        Subscription subscription = Subscription.builder()
                .id(1L)
                .userId(userId)
                .plan(monthlyPlan)
                .tier(silverTier)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusDays(30))
                .build();

        when(subscriptionRepository.findTopByUserIdAndStatusOrderByCreatedAtDesc(userId, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(subscription));
        when(tierRepository.findById(2L)).thenReturn(Optional.of(goldTier));
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(subscription);
        when(benefitService.getEffectiveBenefits(userId)).thenReturn(Collections.emptyList());
        when(subscriptionMapper.toResponse(any(Subscription.class), any())).thenReturn(mockResponse);

        SubscriptionResponse response = subscriptionService.upgradeTier(userId, 2L);

        assertNotNull(response);
        verify(subscriptionRepository).save(any(Subscription.class));
    }

    @Test
    void upgradeTier_downgradeTarget_throwsException() {
        Long userId = 1L;
        Subscription subscription = Subscription.builder()
                .id(1L)
                .userId(userId)
                .plan(monthlyPlan)
                .tier(goldTier)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusDays(30))
                .build();

        when(subscriptionRepository.findTopByUserIdAndStatusOrderByCreatedAtDesc(userId, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(subscription));
        when(tierRepository.findById(1L)).thenReturn(Optional.of(silverTier));

        assertThrows(InvalidTierTransitionException.class,
                () -> subscriptionService.upgradeTier(userId, 1L));
    }

    @Test
    void downgradeTier_validDowngrade_returnsUpdatedSubscription() {
        Long userId = 1L;
        Subscription subscription = Subscription.builder()
                .id(1L)
                .userId(userId)
                .plan(monthlyPlan)
                .tier(goldTier)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusDays(30))
                .build();

        when(subscriptionRepository.findTopByUserIdAndStatusOrderByCreatedAtDesc(userId, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(subscription));
        when(tierRepository.findById(1L)).thenReturn(Optional.of(silverTier));
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(subscription);
        when(benefitService.getEffectiveBenefits(userId)).thenReturn(Collections.emptyList());
        when(subscriptionMapper.toResponse(any(Subscription.class), any())).thenReturn(mockResponse);

        SubscriptionResponse response = subscriptionService.downgradeTier(userId, 1L);

        assertNotNull(response);
        verify(subscriptionRepository).save(any(Subscription.class));
    }

    @Test
    void downgradeTier_upgradeTarget_throwsException() {
        Long userId = 1L;
        Subscription subscription = Subscription.builder()
                .id(1L)
                .userId(userId)
                .plan(monthlyPlan)
                .tier(silverTier)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusDays(30))
                .build();

        when(subscriptionRepository.findTopByUserIdAndStatusOrderByCreatedAtDesc(userId, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(subscription));
        when(tierRepository.findById(2L)).thenReturn(Optional.of(goldTier));

        assertThrows(InvalidTierTransitionException.class,
                () -> subscriptionService.downgradeTier(userId, 2L));
    }

    @Test
    void cancelSubscription_activeSubscription_cancelsSuccessfully() {
        Long userId = 1L;
        Subscription subscription = Subscription.builder()
                .id(1L)
                .userId(userId)
                .plan(monthlyPlan)
                .tier(silverTier)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusDays(30))
                .build();

        when(subscriptionRepository.findTopByUserIdAndStatusOrderByCreatedAtDesc(userId, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(subscription));
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(subscription);

        subscriptionService.cancelSubscription(userId);

        verify(subscriptionRepository).save(any(Subscription.class));
    }

    @Test
    void expireSubscriptions_findsAndExpiresSubscriptions() {
        Subscription subscription = Subscription.builder()
                .id(1L)
                .userId(1L)
                .plan(monthlyPlan)
                .tier(silverTier)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDate.now().minusDays(31))
                .expiryDate(LocalDate.now().minusDays(1))
                .build();

        when(subscriptionRepository.findByStatusAndExpiryDateBefore(SubscriptionStatus.ACTIVE, LocalDate.now()))
                .thenReturn(List.of(subscription));

        subscriptionService.expireSubscriptions();

        verify(subscriptionRepository).saveAll(any());
    }

    @Test
    void autoRenewSubscriptions_findsAndRenewsSubscriptions() {
        Subscription subscription = Subscription.builder()
                .id(1L)
                .userId(1L)
                .plan(monthlyPlan)
                .tier(silverTier)
                .status(SubscriptionStatus.ACTIVE)
                .autoRenew(true)
                .startDate(LocalDate.now().minusDays(30))
                .expiryDate(LocalDate.now().minusDays(1))
                .build();

        when(subscriptionRepository.findByStatusAndAutoRenewTrueAndExpiryDateBefore(SubscriptionStatus.ACTIVE, LocalDate.now()))
                .thenReturn(List.of(subscription));

        subscriptionService.autoRenewSubscriptions();

        verify(subscriptionRepository).saveAll(any());
    }
}