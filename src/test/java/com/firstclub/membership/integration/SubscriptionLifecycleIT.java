package com.firstclub.membership.integration;

import com.firstclub.membership.dto.DowngradeTierRequest;
import com.firstclub.membership.dto.SubscribeRequest;
import com.firstclub.membership.dto.SubscriptionResponse;
import com.firstclub.membership.dto.UpgradeTierRequest;
import com.firstclub.membership.enums.SubscriptionStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

public class SubscriptionLifecycleIT extends BaseIntegrationTest {

    @Test
    void subscribe_shouldCreateSubscription() {
        RestTemplate template = getRestTemplate();
        SubscribeRequest request = new SubscribeRequest(1001L, 1L, 1L);

        ResponseEntity<SubscriptionResponse> response = template.exchange(
                baseUrl() + "/api/v1/subscriptions",
                HttpMethod.POST,
                new HttpEntity<>(request, jsonHeaders()),
                SubscriptionResponse.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1001L, response.getBody().getUserId());
        assertEquals(SubscriptionStatus.ACTIVE, response.getBody().getStatus());
        assertNotNull(response.getBody().getPlan());
        assertNotNull(response.getBody().getTier());
    }

    @Test
    void getSubscription_shouldReturnCurrentSubscription() {
        RestTemplate template = getRestTemplate();

        // First subscribe
        SubscribeRequest subscribeRequest = new SubscribeRequest(1002L, 1L, 1L);
        template.exchange(
                baseUrl() + "/api/v1/subscriptions",
                HttpMethod.POST,
                new HttpEntity<>(subscribeRequest, jsonHeaders()),
                SubscriptionResponse.class
        );

        // Then get subscription
        ResponseEntity<SubscriptionResponse> response = template.exchange(
                baseUrl() + "/api/v1/subscriptions/1002",
                HttpMethod.GET,
                new HttpEntity<>(jsonHeaders()),
                SubscriptionResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1002L, response.getBody().getUserId());
        assertEquals(SubscriptionStatus.ACTIVE, response.getBody().getStatus());
    }

    @Test
    void upgradeTier_shouldChangeTier() {
        RestTemplate template = getRestTemplate();

        // Subscribe at Silver (tierId = 1)
        SubscribeRequest subscribeRequest = new SubscribeRequest(1003L, 1L, 1L);
        template.exchange(
                baseUrl() + "/api/v1/subscriptions",
                HttpMethod.POST,
                new HttpEntity<>(subscribeRequest, jsonHeaders()),
                SubscriptionResponse.class
        );

        // Upgrade to Gold (tierId = 2)
        UpgradeTierRequest upgradeRequest = new UpgradeTierRequest(2L);
        ResponseEntity<SubscriptionResponse> response = template.exchange(
                baseUrl() + "/api/v1/subscriptions/1003/upgrade",
                HttpMethod.PUT,
                new HttpEntity<>(upgradeRequest, jsonHeaders()),
                SubscriptionResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("GOLD", response.getBody().getTier().getName());
        assertEquals(2L, response.getBody().getTier().getId());
    }

    @Test
    void downgradeTier_shouldChangeTier() {
        RestTemplate template = getRestTemplate();

        // Subscribe at Gold (tierId = 2)
        SubscribeRequest subscribeRequest = new SubscribeRequest(1004L, 1L, 2L);
        template.exchange(
                baseUrl() + "/api/v1/subscriptions",
                HttpMethod.POST,
                new HttpEntity<>(subscribeRequest, jsonHeaders()),
                SubscriptionResponse.class
        );

        // Downgrade to Silver (tierId = 1)
        DowngradeTierRequest downgradeRequest = new DowngradeTierRequest(1L);
        ResponseEntity<SubscriptionResponse> response = template.exchange(
                baseUrl() + "/api/v1/subscriptions/1004/downgrade",
                HttpMethod.PUT,
                new HttpEntity<>(downgradeRequest, jsonHeaders()),
                SubscriptionResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("SILVER", response.getBody().getTier().getName());
        assertEquals(1L, response.getBody().getTier().getId());
    }

    @Test
    void cancelSubscription_shouldSetCancelled() {
        RestTemplate template = getRestTemplate();

        // Subscribe
        SubscribeRequest subscribeRequest = new SubscribeRequest(1005L, 1L, 1L);
        template.exchange(
                baseUrl() + "/api/v1/subscriptions",
                HttpMethod.POST,
                new HttpEntity<>(subscribeRequest, jsonHeaders()),
                SubscriptionResponse.class
        );

        // Cancel
        ResponseEntity<Void> cancelResponse = template.exchange(
                baseUrl() + "/api/v1/subscriptions/1005/cancel",
                HttpMethod.PUT,
                new HttpEntity<>(jsonHeaders()),
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, cancelResponse.getStatusCode());

        // Verify subscription is cancelled - should return the subscription with CANCELLED status
        ResponseEntity<SubscriptionResponse> getResponse = template.exchange(
                baseUrl() + "/api/v1/subscriptions/1005",
                HttpMethod.GET,
                new HttpEntity<>(jsonHeaders()),
                SubscriptionResponse.class
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals(SubscriptionStatus.CANCELLED, getResponse.getBody().getStatus());
    }
}