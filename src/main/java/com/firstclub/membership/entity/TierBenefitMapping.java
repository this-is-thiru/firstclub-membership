package com.firstclub.membership.entity;

import com.firstclub.membership.audit.AuditableEntity;
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
@Table(name = "tier_benefit_mapping",
        uniqueConstraints = @UniqueConstraint(columnNames = {"tier_id", "benefit_id"}))
public class TierBenefitMapping extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id", nullable = false)
    private MembershipTier tier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "benefit_id", nullable = false)
    private MembershipBenefit benefit;
}