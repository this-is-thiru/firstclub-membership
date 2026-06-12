package com.firstclub.membership.integration;

import com.firstclub.membership.dto.SubscribeRequest;
import com.firstclub.membership.dto.SubscriptionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BenefitRetrievalIT extends BaseIntegrationTest {

    @Test
    void getBenefits_shouldReturnTierBenefits() {
        RestTemplate template = getRestTemplate();

        // Subscribe at Gold (tierId = 2) - has FREE_DELIVERY and EXTRA_DISCOUNT
        SubscribeRequest subscribeRequest = new SubscribeRequest(3001L, 1L, 2L);
        template.exchange(
                baseUrl() + "/api/v1/subscriptions",
                HttpMethod.POST,
                new HttpEntity<>(subscribeRequest, jsonHeaders()),
                SubscriptionResponse.class
        );

        // Get benefits
        ResponseEntity<List> response = template.exchange(
                baseUrl() + "/api/v1/users/3001/benefits",
                HttpMethod.GET,
                new HttpEntity<>(jsonHeaders()),
                List.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());

        // Check that FREE_DELIVERY is present
        boolean hasFreeDelivery = response.getBody().stream()
                .anyMatch(item -> {
                    if (item instanceof Map) {
                        return "FREE_DELIVERY".equals(((Map<?, ?>) item).get("type"));
                    }
                    return false;
                });
        assertTrue(hasFreeDelivery, "Expected FREE_DELIVERY benefit for GOLD tier");

        // Check that EXTRA_DISCOUNT is present
        boolean hasExtraDiscount = response.getBody().stream()
                .anyMatch(item -> {
                    if (item instanceof Map) {
                        return "EXTRA_DISCOUNT".equals(((Map<?, ?>) item).get("type"));
                    }
                    return false;
                });
        assertTrue(hasExtraDiscount, "Expected EXTRA_DISCOUNT benefit for GOLD tier");
    }
}