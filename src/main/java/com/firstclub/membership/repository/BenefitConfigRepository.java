package com.firstclub.membership.repository;

import com.firstclub.membership.entity.BenefitConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BenefitConfigRepository extends JpaRepository<BenefitConfig, Long> {
}