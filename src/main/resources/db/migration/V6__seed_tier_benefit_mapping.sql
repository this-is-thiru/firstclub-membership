-- V6__seed_tier_benefit_mapping.sql
-- Seed tier-benefit mappings
-- Silver -> FreeDelivery
-- Gold -> FreeDelivery + ExtraDiscount
-- Platinum -> FreeDelivery + ExtraDiscount + PrioritySupport

INSERT INTO tier_benefit_mapping (tier_id, benefit_id, created_at, updated_at)
VALUES
    ((SELECT id FROM membership_tier WHERE name = 'SILVER'), (SELECT id FROM membership_benefit WHERE type = 'FREE_DELIVERY'), NOW(), NOW()),
    ((SELECT id FROM membership_tier WHERE name = 'GOLD'), (SELECT id FROM membership_benefit WHERE type = 'FREE_DELIVERY'), NOW(), NOW()),
    ((SELECT id FROM membership_tier WHERE name = 'GOLD'), (SELECT id FROM membership_benefit WHERE type = 'EXTRA_DISCOUNT'), NOW(), NOW()),
    ((SELECT id FROM membership_tier WHERE name = 'PLATINUM'), (SELECT id FROM membership_benefit WHERE type = 'FREE_DELIVERY'), NOW(), NOW()),
    ((SELECT id FROM membership_tier WHERE name = 'PLATINUM'), (SELECT id FROM membership_benefit WHERE type = 'EXTRA_DISCOUNT'), NOW(), NOW()),
    ((SELECT id FROM membership_tier WHERE name = 'PLATINUM'), (SELECT id FROM membership_benefit WHERE type = 'PRIORITY_SUPPORT'), NOW(), NOW());