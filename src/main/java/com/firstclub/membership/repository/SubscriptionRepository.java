package com.firstclub.membership.repository;

import com.firstclub.membership.entity.Subscription;
import com.firstclub.membership.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findTopByUserIdAndStatusOrderByCreatedAtDesc(Long userId, SubscriptionStatus status);

    Optional<Subscription> findTopByUserIdOrderByCreatedAtDesc(Long userId);

    List<Subscription> findByStatusAndExpiryDateBefore(SubscriptionStatus status, LocalDate date);

    List<Subscription> findByStatusAndAutoRenewTrueAndExpiryDateBefore(SubscriptionStatus status, LocalDate date);
}