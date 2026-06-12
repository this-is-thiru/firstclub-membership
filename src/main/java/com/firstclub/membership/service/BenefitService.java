package com.firstclub.membership.service;

import com.firstclub.membership.dto.BenefitResult;

import java.util.List;

public interface BenefitService {
    List<BenefitResult> getEffectiveBenefits(Long userId);
}