package com.firstclub.membership.provider;

import java.math.BigDecimal;

public interface OrderMetricsProvider {
    int getMonthlyOrderCount(Long userId);
    BigDecimal getMonthlySpend(Long userId);
    String getUserCohort(Long userId);
}