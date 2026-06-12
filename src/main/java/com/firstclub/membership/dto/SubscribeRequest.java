package com.firstclub.membership.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscribeRequest {
    @NotNull
    private Long userId;
    @NotNull
    private Long planId;
    @NotNull
    private Long tierId;
}