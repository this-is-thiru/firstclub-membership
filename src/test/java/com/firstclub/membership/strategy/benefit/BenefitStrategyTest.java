package com.firstclub.membership.strategy.benefit;

import com.firstclub.membership.dto.BenefitResult;
import com.firstclub.membership.entity.MembershipBenefit;
import com.firstclub.membership.enums.BenefitType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BenefitStrategyTest {

    private BenefitStrategyFactory factory;

    @BeforeEach
    void setUp() {
        List<BenefitStrategy> strategies = List.of(
                new FreeDeliveryBenefitStrategy(),
                new DiscountBenefitStrategy(),
                new EarlyAccessBenefitStrategy(),
                new ExclusiveDealsBenefitStrategy(),
                new PrioritySupportBenefitStrategy()
        );
        factory = new BenefitStrategyFactory(strategies);
    }

    @Test
    void freeDeliveryStrategy_returnsCorrectType() {
        FreeDeliveryBenefitStrategy strategy = new FreeDeliveryBenefitStrategy();
        assertEquals(BenefitType.FREE_DELIVERY, strategy.getType());
    }

    @Test
    void discountStrategy_returnsCorrectType() {
        DiscountBenefitStrategy strategy = new DiscountBenefitStrategy();
        assertEquals(BenefitType.EXTRA_DISCOUNT, strategy.getType());
    }

    @Test
    void earlyAccessStrategy_returnsCorrectType() {
        EarlyAccessBenefitStrategy strategy = new EarlyAccessBenefitStrategy();
        assertEquals(BenefitType.EARLY_ACCESS, strategy.getType());
    }

    @Test
    void exclusiveDealsStrategy_returnsCorrectType() {
        ExclusiveDealsBenefitStrategy strategy = new ExclusiveDealsBenefitStrategy();
        assertEquals(BenefitType.EXCLUSIVE_DEALS, strategy.getType());
    }

    @Test
    void prioritySupportStrategy_returnsCorrectType() {
        PrioritySupportBenefitStrategy strategy = new PrioritySupportBenefitStrategy();
        assertEquals(BenefitType.PRIORITY_SUPPORT, strategy.getType());
    }

    @Test
    void freeDeliveryStrategy_apply_returnsBenefitResult() {
        FreeDeliveryBenefitStrategy strategy = new FreeDeliveryBenefitStrategy();
        MembershipBenefit benefit = MembershipBenefit.builder()
                .name("Free Delivery")
                .description("Free delivery on all orders")
                .build();
        Map<String, String> config = new HashMap<>();
        config.put("maxOrders", "999");

        BenefitResult result = strategy.apply(benefit, config);

        assertEquals(BenefitType.FREE_DELIVERY, result.getType());
        assertEquals("Free Delivery", result.getName());
        assertEquals("Free delivery on all orders", result.getDescription());
        assertEquals("999", result.getMeta().get("maxOrders"));
    }

    @Test
    void discountStrategy_apply_returnsBenefitResult() {
        DiscountBenefitStrategy strategy = new DiscountBenefitStrategy();
        MembershipBenefit benefit = MembershipBenefit.builder()
                .name("Extra Discount")
                .description("Additional 10% off")
                .build();
        Map<String, String> config = Map.of("discount_percentage", "10");

        BenefitResult result = strategy.apply(benefit, config);

        assertEquals(BenefitType.EXTRA_DISCOUNT, result.getType());
        assertEquals("10", result.getMeta().get("discount_percentage"));
    }

    @Test
    void factory_getStrategy_returnsCorrectStrategy() {
        assertInstanceOf(FreeDeliveryBenefitStrategy.class, factory.getStrategy(BenefitType.FREE_DELIVERY));
        assertInstanceOf(DiscountBenefitStrategy.class, factory.getStrategy(BenefitType.EXTRA_DISCOUNT));
        assertInstanceOf(EarlyAccessBenefitStrategy.class, factory.getStrategy(BenefitType.EARLY_ACCESS));
        assertInstanceOf(ExclusiveDealsBenefitStrategy.class, factory.getStrategy(BenefitType.EXCLUSIVE_DEALS));
        assertInstanceOf(PrioritySupportBenefitStrategy.class, factory.getStrategy(BenefitType.PRIORITY_SUPPORT));
    }

    @Test
    void factory_getStrategy_unknownType_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                factory.getStrategy(null));
    }
}