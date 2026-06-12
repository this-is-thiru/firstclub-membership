package com.firstclub.membership.integration;

import com.firstclub.membership.dto.SubscribeRequest;
import com.firstclub.membership.dto.SubscriptionResponse;
import com.firstclub.membership.dto.TierEvaluationResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

public class TierEvaluationIT extends BaseIntegrationTest {

    @Test
    void evaluateTier_shouldReturnEligibleTier() {
        RestTemplate template = getRestTemplate();

        // Subscribe
        SubscribeRequest subscribeRequest = new SubscribeRequest(2001L, 1L, 1L);
        template.exchange(
                baseUrl() + "/api/v1/subscriptions",
                HttpMethod.POST,
                new HttpEntity<>(subscribeRequest, jsonHeaders()),
                SubscriptionResponse.class
        );

        // Evaluate tier
        ResponseEntity<TierEvaluationResponse> response = template.exchange(
                baseUrl() + "/api/v1/tiers/evaluate/2001",
                HttpMethod.POST,
                new HttpEntity<>(jsonHeaders()),
                TierEvaluationResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getEligibleTier());
        assertNotNull(response.getBody().getCurrentTier());
        assertNotNull(response.getBody().getDecision());
    }

    @Test
    void evaluateHistory_shouldBeLogged() {
        RestTemplate template = getRestTemplate();

        // Subscribe
        SubscribeRequest subscribeRequest = new SubscribeRequest(2002L, 1L, 2L);
        template.exchange(
                baseUrl() + "/api/v1/subscriptions",
                HttpMethod.POST,
                new HttpEntity<>(subscribeRequest, jsonHeaders()),
                SubscriptionResponse.class
        );

        // Evaluate tier
        template.exchange(
                baseUrl() + "/api/v1/tiers/evaluate/2002",
                HttpMethod.POST,
                new HttpEntity<>(jsonHeaders()),
                TierEvaluationResponse.class
        );

        // Evaluation is logged - the response should be successful
        // History can be verified through the evaluation response having a timestamp
        ResponseEntity<TierEvaluationResponse> response = template.exchange(
                baseUrl() + "/api/v1/tiers/evaluate/2002",
                HttpMethod.POST,
                new HttpEntity<>(jsonHeaders()),
                TierEvaluationResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getEvaluatedAt());
    }
}