-- V5__seed_benefit_configs.sql
-- Seed benefit configurations for EXTRA_DISCOUNT

INSERT INTO benefit_config (benefit_id, config_key, config_value, created_at, updated_at)
SELECT id, 'discount_percentage', '10', NOW(), NOW() FROM membership_benefit WHERE type = 'EXTRA_DISCOUNT';