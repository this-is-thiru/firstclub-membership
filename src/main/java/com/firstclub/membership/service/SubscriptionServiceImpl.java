package com.firstclub.membership.service;

import com.firstclub.membership.dto.BenefitResult;
import com.firstclub.membership.dto.SubscribeRequest;
import com.firstclub.membership.dto.SubscriptionResponse;
import com.firstclub.membership.entity.MembershipPlan;
import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.entity.Subscription;
import com.firstclub.membership.enums.SubscriptionStatus;
import com.firstclub.membership.exception.ConcurrentModificationException;
import com.firstclub.membership.exception.InvalidTierTransitionException;
import com.firstclub.membership.exception.PlanNotFoundException;
import com.firstclub.membership.exception.SubscriptionNotFoundException;
import com.firstclub.membership.exception.TierNotFoundException;
import com.firstclub.membership.mapper.SubscriptionMapper;
import com.firstclub.membership.repository.MembershipPlanRepository;
import com.firstclub.membership.repository.MembershipTierRepository;
import com.firstclub.membership.repository.SubscriptionRepository;
import jakarta.persistence.OptimisticLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final MembershipPlanRepository planRepository;
    private final MembershipTierRepository tierRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final BenefitService benefitService;

    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository,
                                   MembershipPlanRepository planRepository,
                                   MembershipTierRepository tierRepository,
                                   SubscriptionMapper subscriptionMapper,
                                   BenefitService benefitService) {
        this.subscriptionRepository = subscriptionRepository;
        this.planRepository = planRepository;
        this.tierRepository = tierRepository;
        this.subscriptionMapper = subscriptionMapper;
        this.benefitService = benefitService;
    }

    @Override
    @Transactional
    public SubscriptionResponse subscribe(SubscribeRequest req) {
        MembershipPlan plan = planRepository.findById(req.getPlanId())
                .orElseThrow(() -> new PlanNotFoundException("Plan not found: " + req.getPlanId()));

        if (!plan.getActive()) {
            throw new PlanNotFoundException("Plan is not active: " + req.getPlanId());
        }

        MembershipTier tier = tierRepository.findById(req.getTierId())
                .orElseThrow(() -> new TierNotFoundException("Tier not found: " + req.getTierId()));

        if (!tier.getActive()) {
            throw new TierNotFoundException("Tier is not active: " + req.getTierId());
        }

        subscriptionRepository.findTopByUserIdAndStatusOrderByCreatedAtDesc(req.getUserId(), SubscriptionStatus.ACTIVE)
                .ifPresent(existing -> {
                    throw new com.firstclub.membership.exception.MembershipException(
                            "User already has an active subscription");
                });

        LocalDate startDate = LocalDate.now();
        LocalDate expiryDate = startDate.plusDays(plan.getDurationDays());

        Subscription subscription = Subscription.builder()
                .userId(req.getUserId())
                .plan(plan)
                .tier(tier)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(startDate)
                .expiryDate(expiryDate)
                .autoRenew(false)
                .build();

        Subscription saved = subscriptionRepository.save(subscription);
        List<BenefitResult> benefits = benefitService.getEffectiveBenefits(req.getUserId());
        return subscriptionMapper.toResponse(saved, benefits);
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionResponse getCurrentSubscription(Long userId) {
        Subscription subscription = subscriptionRepository
                .findTopByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new SubscriptionNotFoundException("No subscription found for user: " + userId));

        List<BenefitResult> benefits = benefitService.getEffectiveBenefits(userId);
        return subscriptionMapper.toResponse(subscription, benefits);
    }

    @Override
    @Transactional
    public SubscriptionResponse upgradeTier(Long userId, Long targetTierId) {
        try {
            Subscription subscription = subscriptionRepository
                    .findTopByUserIdAndStatusOrderByCreatedAtDesc(userId, SubscriptionStatus.ACTIVE)
                    .orElseThrow(() -> new SubscriptionNotFoundException("No active subscription found for user: " + userId));

            MembershipTier targetTier = tierRepository.findById(targetTierId)
                    .orElseThrow(() -> new TierNotFoundException("Tier not found: " + targetTierId));

            if (!targetTier.getActive()) {
                throw new TierNotFoundException("Target tier is not active: " + targetTierId);
            }

            MembershipTier currentTier = subscription.getTier();
            if (targetTier.getPriority() <= currentTier.getPriority()) {
                throw new InvalidTierTransitionException(
                        "Target tier must have higher priority than current tier for upgrade");
            }

            subscription.setTier(targetTier);
            Subscription saved = subscriptionRepository.save(subscription);
            List<BenefitResult> benefits = benefitService.getEffectiveBenefits(userId);
            return subscriptionMapper.toResponse(saved, benefits);
        } catch (OptimisticLockException e) {
            throw new ConcurrentModificationException("Subscription was modified by another transaction");
        }
    }

    @Override
    @Transactional
    public SubscriptionResponse downgradeTier(Long userId, Long targetTierId) {
        try {
            Subscription subscription = subscriptionRepository
                    .findTopByUserIdAndStatusOrderByCreatedAtDesc(userId, SubscriptionStatus.ACTIVE)
                    .orElseThrow(() -> new SubscriptionNotFoundException("No active subscription found for user: " + userId));

            MembershipTier targetTier = tierRepository.findById(targetTierId)
                    .orElseThrow(() -> new TierNotFoundException("Tier not found: " + targetTierId));

            if (!targetTier.getActive()) {
                throw new TierNotFoundException("Target tier is not active: " + targetTierId);
            }

            MembershipTier currentTier = subscription.getTier();
            if (targetTier.getPriority() >= currentTier.getPriority()) {
                throw new InvalidTierTransitionException(
                        "Target tier must have lower priority than current tier for downgrade");
            }

            subscription.setTier(targetTier);
            Subscription saved = subscriptionRepository.save(subscription);
            List<BenefitResult> benefits = benefitService.getEffectiveBenefits(userId);
            return subscriptionMapper.toResponse(saved, benefits);
        } catch (OptimisticLockException e) {
            throw new ConcurrentModificationException("Subscription was modified by another transaction");
        }
    }

    @Override
    @Transactional
    public void cancelSubscription(Long userId) {
        try {
            Subscription subscription = subscriptionRepository
                    .findTopByUserIdAndStatusOrderByCreatedAtDesc(userId, SubscriptionStatus.ACTIVE)
                    .orElseThrow(() -> new SubscriptionNotFoundException("No active subscription found for user: " + userId));

            subscription.setStatus(SubscriptionStatus.CANCELLED);
            subscription.setCancelledAt(LocalDateTime.now());
            subscriptionRepository.save(subscription);
        } catch (OptimisticLockException e) {
            throw new ConcurrentModificationException("Subscription was modified by another transaction");
        }
    }

    @Override
    @Transactional
    public void expireSubscriptions() {
        List<Subscription> expiredSubscriptions = subscriptionRepository
                .findByStatusAndExpiryDateBefore(SubscriptionStatus.ACTIVE, LocalDate.now());

        for (Subscription subscription : expiredSubscriptions) {
            subscription.setStatus(SubscriptionStatus.EXPIRED);
        }

        subscriptionRepository.saveAll(expiredSubscriptions);
    }

    @Override
    @Transactional
    public void autoRenewSubscriptions() {
        List<Subscription> subscriptionsToRenew = subscriptionRepository
                .findByStatusAndAutoRenewTrueAndExpiryDateBefore(SubscriptionStatus.ACTIVE, LocalDate.now());

        for (Subscription subscription : subscriptionsToRenew) {
            LocalDate newExpiryDate = subscription.getExpiryDate()
                    .plusDays(subscription.getPlan().getDurationDays());
            subscription.setExpiryDate(newExpiryDate);
        }

        subscriptionRepository.saveAll(subscriptionsToRenew);
    }
}