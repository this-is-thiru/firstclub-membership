package com.firstclub.membership.repository;

import com.firstclub.membership.entity.MembershipBenefit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipBenefitRepository extends JpaRepository<MembershipBenefit, Long> {
}