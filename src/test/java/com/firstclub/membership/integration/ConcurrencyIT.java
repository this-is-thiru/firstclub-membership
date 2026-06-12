package com.firstclub.membership.integration;

import com.firstclub.membership.dto.SubscribeRequest;
import com.firstclub.membership.dto.SubscriptionResponse;
import com.firstclub.membership.dto.UpgradeTierRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

public class ConcurrencyIT extends BaseIntegrationTest {

    @Test
    void concurrentUpgrade_shouldHandleCorrectly() throws InterruptedException {
        RestTemplate template = getRestTemplate();

        // Subscribe at Silver (tierId = 1)
        SubscribeRequest subscribeRequest = new SubscribeRequest(4001L, 1L, 1L);
        ResponseEntity<SubscriptionResponse> subscribeResponse = template.exchange(
                baseUrl() + "/api/v1/subscriptions",
                HttpMethod.POST,
                new HttpEntity<>(subscribeRequest, jsonHeaders()),
                SubscriptionResponse.class
        );
        assertEquals(201, subscribeResponse.getStatusCode().value());

        // Two concurrent upgrade attempts to Gold (tierId = 2)
        UpgradeTierRequest upgradeRequest = new UpgradeTierRequest(2L);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);
        List<HttpStatusCode> statuses = Collections.synchronizedList(new ArrayList<>());

        // Thread 1
        executor.submit(() -> {
            try {
                ResponseEntity<SubscriptionResponse> r = template.exchange(
                        baseUrl() + "/api/v1/subscriptions/4001/upgrade",
                        HttpMethod.PUT,
                        new HttpEntity<>(upgradeRequest, jsonHeaders()),
                        SubscriptionResponse.class
                );
                statuses.add(r.getStatusCode());
            } finally {
                latch.countDown();
            }
        });

        // Thread 2
        executor.submit(() -> {
            try {
                ResponseEntity<SubscriptionResponse> r = template.exchange(
                        baseUrl() + "/api/v1/subscriptions/4001/upgrade",
                        HttpMethod.PUT,
                        new HttpEntity<>(upgradeRequest, jsonHeaders()),
                        SubscriptionResponse.class
                );
                statuses.add(r.getStatusCode());
            } finally {
                latch.countDown();
            }
        });

        latch.await();
        executor.shutdown();

        // At least one should succeed (200)
        long successCount = statuses.stream()
                .filter(s -> s.value() == 200).count();
        assertTrue(successCount >= 1, "At least one upgrade should succeed");

        // Both requests should complete
        assertEquals(2, statuses.size());
    }
}