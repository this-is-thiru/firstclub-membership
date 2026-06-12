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
@Table(name = "benefit_config")
public class BenefitConfig extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "benefit_id", nullable = false)
    private MembershipBenefit benefit;

    @Column(nullable = false)
    private String configKey;

    private String configValue;
}