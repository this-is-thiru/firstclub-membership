package com.firstclub.membership.controller;

import com.firstclub.membership.dto.DowngradeTierRequest;
import com.firstclub.membership.dto.SubscribeRequest;
import com.firstclub.membership.dto.SubscriptionResponse;
import com.firstclub.membership.dto.UpgradeTierRequest;
import com.firstclub.membership.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/subscriptions")
@Tag(name = "Subscriptions", description = "Membership subscription management")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Subscribe a user to a membership plan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Subscription created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(name = "Subscribe Success",
                                    value = """
                                            {
                                              "id": 1,
                                              "userId": 123,
                                              "plan": {"id": 1, "name": "MONTHLY", "durationDays": 30, "price": 299.00},
                                              "tier": {"id": 1, "name": "SILVER", "priority": 1},
                                              "status": "ACTIVE",
                                              "startDate": "2026-06-10",
                                              "expiryDate": "2026-07-10",
                                              "autoRenew": false,
                                              "benefits": [],
                                              "version": 0
                                            }
                                            """))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Plan or tier not found")
    })
    public ResponseEntity<SubscriptionResponse> subscribe(@Valid @RequestBody SubscribeRequest req) {
        SubscriptionResponse response = subscriptionService.subscribe(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get current subscription for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved subscription",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(name = "Get Subscription Success",
                                    value = """
                                            {
                                              "id": 1,
                                              "userId": 123,
                                              "plan": {"id": 1, "name": "MONTHLY", "durationDays": 30, "price": 299.00},
                                              "tier": {"id": 2, "name": "GOLD", "priority": 2},
                                              "status": "ACTIVE",
                                              "startDate": "2026-06-01",
                                              "expiryDate": "2026-07-01",
                                              "autoRenew": true,
                                              "benefits": [],
                                              "version": 1
                                            }
                                            """))),
            @ApiResponse(responseCode = "404", description = "Subscription not found")
    })
    public ResponseEntity<SubscriptionResponse> getCurrentSubscription(@PathVariable Long userId) {
        return ResponseEntity.ok(subscriptionService.getCurrentSubscription(userId));
    }

    @PutMapping(value = "/{userId}/upgrade", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Upgrade user to a higher tier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tier upgraded successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(name = "Upgrade Success",
                                    value = """
                                            {
                                              "id": 1,
                                              "userId": 123,
                                              "plan": {"id": 1, "name": "MONTHLY", "durationDays": 30, "price": 299.00},
                                              "tier": {"id": 3, "name": "PLATINUM", "priority": 3},
                                              "status": "ACTIVE",
                                              "startDate": "2026-06-01",
                                              "expiryDate": "2026-07-01",
                                              "autoRenew": true,
                                              "benefits": [],
                                              "version": 2
                                            }
                                            """))),
            @ApiResponse(responseCode = "400", description = "Invalid tier transition"),
            @ApiResponse(responseCode = "404", description = "Subscription or tier not found")
    })
    public ResponseEntity<SubscriptionResponse> upgradeTier(
            @PathVariable Long userId,
            @Valid @RequestBody UpgradeTierRequest req) {
        return ResponseEntity.ok(subscriptionService.upgradeTier(userId, req.getTargetTierId()));
    }

    @PutMapping(value = "/{userId}/downgrade", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Downgrade user to a lower tier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tier downgraded successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(name = "Downgrade Success",
                                    value = """
                                            {
                                              "id": 1,
                                              "userId": 123,
                                              "plan": {"id": 1, "name": "MONTHLY", "durationDays": 30, "price": 299.00},
                                              "tier": {"id": 1, "name": "SILVER", "priority": 1},
                                              "status": "ACTIVE",
                                              "startDate": "2026-06-01",
                                              "expiryDate": "2026-07-01",
                                              "autoRenew": false,
                                              "benefits": [],
                                              "version": 2
                                            }
                                            """))),
            @ApiResponse(responseCode = "400", description = "Invalid tier transition"),
            @ApiResponse(responseCode = "404", description = "Subscription or tier not found")
    })
    public ResponseEntity<SubscriptionResponse> downgradeTier(
            @PathVariable Long userId,
            @Valid @RequestBody DowngradeTierRequest req) {
        return ResponseEntity.ok(subscriptionService.downgradeTier(userId, req.getTargetTierId()));
    }

    @PutMapping("/{userId}/cancel")
    @Operation(summary = "Cancel user's subscription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Subscription cancelled successfully"),
            @ApiResponse(responseCode = "404", description = "Subscription not found")
    })
    public ResponseEntity<Void> cancelSubscription(@PathVariable Long userId) {
        subscriptionService.cancelSubscription(userId);
        return ResponseEntity.noContent().build();
    }
}