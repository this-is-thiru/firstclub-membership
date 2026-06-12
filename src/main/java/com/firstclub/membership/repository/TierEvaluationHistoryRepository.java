package com.firstclub.membership.repository;

import com.firstclub.membership.entity.TierEvaluationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TierEvaluationHistoryRepository extends JpaRepository<TierEvaluationHistory, Long> {
    List<TierEvaluationHistory> findByUserIdOrderByEvaluatedAtDesc(Long userId);
}