package com.firstclub.membership.provider;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;

@Primary
@Service
public class InMemoryOrderMetricsProvider implements OrderMetricsProvider {

    private final ConcurrentHashMap<Long, Integer> orderCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, BigDecimal> monthlySpends = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, String> cohorts = new ConcurrentHashMap<>();

    public InMemoryOrderMetricsProvider() {
        // Initialize with sample data for demo
        orderCounts.put(1L, 15);
        monthlySpends.put(1L, BigDecimal.valueOf(6000));
        cohorts.put(1L, "PREMIUM_USERS");
    }

    @Override
    public int getMonthlyOrderCount(Long userId) {
        return orderCounts.getOrDefault(userId, 15);
    }

    @Override
    public BigDecimal getMonthlySpend(Long userId) {
        return monthlySpends.getOrDefault(userId, BigDecimal.valueOf(6000));
    }

    @Override
    public String getUserCohort(Long userId) {
        return cohorts.getOrDefault(userId, "PREMIUM_USERS");
    }
}