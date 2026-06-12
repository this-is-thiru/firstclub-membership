package com.firstclub.membership.entity;

import com.firstclub.membership.audit.AuditableEntity;
import com.firstclub.membership.enums.BenefitType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "membership_benefit")
public class MembershipBenefit extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private BenefitType type;

    private String name;

    private String description;

    @OneToMany(mappedBy = "benefit", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BenefitConfig> configs = new ArrayList<>();

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}