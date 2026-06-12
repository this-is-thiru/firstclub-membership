-- V2__seed_plans.sql
-- Seed membership plans: MONTHLY, QUARTERLY, YEARLY

INSERT INTO membership_plan (name, duration_days, price, active, created_at, updated_at)
VALUES
    ('MONTHLY', 30, 299.00, true, NOW(), NOW()),
    ('QUARTERLY', 90, 799.00, true, NOW(), NOW()),
    ('YEARLY', 365, 2499.00, true, NOW(), NOW());