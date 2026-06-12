-- V7__seed_eligibility_rules.sql
-- Seed tier eligibility rules
-- Gold: ORDER_COUNT > 10
-- Platinum: MONTHLY_SPEND > 5000 AND COHORT = PREMIUM_USERS

INSERT INTO tier_eligibility_rule (tier_id, rule_type, rule_operator, rule_value, active, created_at, updated_at)
VALUES
    ((SELECT id FROM membership_tier WHERE name = 'GOLD'), 'ORDER_COUNT', 'GT', '10', true, NOW(), NOW()),
    ((SELECT id FROM membership_tier WHERE name = 'PLATINUM'), 'MONTHLY_SPEND', 'GT', '5000', true, NOW(), NOW()),
    ((SELECT id FROM membership_tier WHERE name = 'PLATINUM'), 'COHORT', 'EQ', 'PREMIUM_USERS', true, NOW(), NOW());