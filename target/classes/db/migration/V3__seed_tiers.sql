-- V3__seed_tiers.sql
-- Seed membership tiers: SILVER, GOLD, PLATINUM

INSERT INTO membership_tier (name, priority, description, active, created_at, updated_at)
VALUES
    ('SILVER', 1, 'Silver tier', true, NOW(), NOW()),
    ('GOLD', 2, 'Gold tier', true, NOW(), NOW()),
    ('PLATINUM', 3, 'Platinum tier', true, NOW(), NOW());