-- V4__seed_benefits.sql
-- Seed membership benefits: all 5 benefit types

INSERT INTO membership_benefit (type, name, description, active, created_at, updated_at)
VALUES
    ('FREE_DELIVERY', 'Free Delivery', 'Free delivery on all orders', true, NOW(), NOW()),
    ('EXTRA_DISCOUNT', 'Extra Discount', 'Additional discount on purchases', true, NOW(), NOW()),
    ('EARLY_ACCESS', 'Early Access', 'Early access to sales', true, NOW(), NOW()),
    ('EXCLUSIVE_DEALS', 'Exclusive Deals', 'Access to exclusive deals', true, NOW(), NOW()),
    ('PRIORITY_SUPPORT', 'Priority Support', 'Priority customer support', true, NOW(), NOW());