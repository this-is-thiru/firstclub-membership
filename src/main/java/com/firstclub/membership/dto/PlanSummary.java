package com.firstclub.membership.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanSummary {
    private Long id;
    private String name;
    private Integer durationDays;
    private BigDecimal price;
}