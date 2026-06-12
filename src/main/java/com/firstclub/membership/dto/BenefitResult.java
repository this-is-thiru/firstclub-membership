package com.firstclub.membership.dto;

import com.firstclub.membership.enums.BenefitType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BenefitResult {
    private BenefitType type;
    private String name;
    private String description;
    private Map<String, String> meta;
}