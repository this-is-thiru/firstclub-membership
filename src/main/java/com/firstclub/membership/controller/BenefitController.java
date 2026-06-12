package com.firstclub.membership.controller;

import com.firstclub.membership.dto.BenefitResult;
import com.firstclub.membership.enums.BenefitType;
import com.firstclub.membership.service.BenefitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users/{userId}/benefits")
@Tag(name = "Benefits", description = "User benefits management")
public class BenefitController {

    private final BenefitService benefitService;

    public BenefitController(BenefitService benefitService) {
        this.benefitService = benefitService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get effective benefits for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved benefits",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(name = "Get Benefits Success",
                                    value = """
                                            [
                                              {
                                                "type": "FREE_DELIVERY",
                                                "name": "Free Delivery",
                                                "description": "Free delivery on all orders",
                                                "meta": {}
                                              },
                                              {
                                                "type": "EXTRA_DISCOUNT",
                                                "name": "Extra Discount",
                                                "description": "Additional discount on purchases",
                                                "meta": {"discount_percentage": "10"}
                                              },
                                              {
                                                "type": "PRIORITY_SUPPORT",
                                                "name": "Priority Support",
                                                "description": "Priority customer support",
                                                "meta": {}
                                              }
                                            ]
                                            """))),
            @ApiResponse(responseCode = "404", description = "User subscription not found")
    })
    public ResponseEntity<List<BenefitResult>> getEffectiveBenefits(@PathVariable Long userId) {
        return ResponseEntity.ok(benefitService.getEffectiveBenefits(userId));
    }
}