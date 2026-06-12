package com.firstclub.membership.repository;

import com.firstclub.membership.entity.TierBenefitMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TierBenefitMappingRepository extends JpaRepository<TierBenefitMapping, Long> {
    List<TierBenefitMapping> findByTierId(Long tierId);
}