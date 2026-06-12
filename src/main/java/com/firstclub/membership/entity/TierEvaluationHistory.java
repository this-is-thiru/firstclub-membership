package com.firstclub.membership.entity;

import com.firstclub.membership.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tier_evaluation_history")
public class TierEvaluationHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private String oldTier;

    private String newTier;

    private String reason;

    @Column(nullable = false)
    private LocalDateTime evaluatedAt;
}