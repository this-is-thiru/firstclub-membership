package com.firstclub.membership.entity;

import com.firstclub.membership.audit.AuditableEntity;
import com.firstclub.membership.enums.RuleOperator;
import com.firstclub.membership.enums.RuleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tier_eligibility_rule")
public class TierEligibilityRule extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id", nullable = false)
    private MembershipTier tier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuleType ruleType;

    @Enumerated(EnumType.STRING)
    @Column(name = "rule_operator", nullable = false)
    private RuleOperator operator;

    @Column(name = "rule_value", nullable = false)
    private String value;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}