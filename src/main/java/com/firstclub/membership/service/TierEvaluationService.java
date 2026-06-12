package com.firstclub.membership.service;

import com.firstclub.membership.dto.TierEvaluationResponse;
import com.firstclub.membership.entity.TierEvaluationHistory;

import java.util.List;

public interface TierEvaluationService {
    TierEvaluationResponse evaluateTier(Long userId);

    List<TierEvaluationHistory> getEvaluationHistory(Long userId);

    void evaluateAllActiveSubscriptions();
}