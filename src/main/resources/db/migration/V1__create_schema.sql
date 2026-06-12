-- V1__create_schema.sql
-- Create all membership service tables (MySQL & H2 compatible)

CREATE TABLE membership_plan (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    duration_days INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE membership_tier (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    priority INT NOT NULL,
    description VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE membership_benefit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255),
    description VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE benefit_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    benefit_id BIGINT NOT NULL,
    config_key VARCHAR(255) NOT NULL,
    config_value VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_benefit_config_benefit FOREIGN KEY (benefit_id) REFERENCES membership_benefit(id) ON DELETE CASCADE
);

CREATE TABLE tier_benefit_mapping (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tier_id BIGINT NOT NULL,
    benefit_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_tier_benefit_mapping_tier FOREIGN KEY (tier_id) REFERENCES membership_tier(id) ON DELETE CASCADE,
    CONSTRAINT fk_tier_benefit_mapping_benefit FOREIGN KEY (benefit_id) REFERENCES membership_benefit(id) ON DELETE CASCADE,
    UNIQUE (tier_id, benefit_id)
);

CREATE TABLE subscription (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    plan_id BIGINT NOT NULL,
    tier_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    auto_renew BOOLEAN NOT NULL DEFAULT FALSE,
    cancelled_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_subscription_plan FOREIGN KEY (plan_id) REFERENCES membership_plan(id),
    CONSTRAINT fk_subscription_tier FOREIGN KEY (tier_id) REFERENCES membership_tier(id)
);

CREATE TABLE tier_eligibility_rule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tier_id BIGINT NOT NULL,
    rule_type VARCHAR(50) NOT NULL,
    rule_operator VARCHAR(10) NOT NULL,
    rule_value VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_tier_eligibility_rule_tier FOREIGN KEY (tier_id) REFERENCES membership_tier(id) ON DELETE CASCADE
);

CREATE TABLE tier_evaluation_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    old_tier VARCHAR(255),
    new_tier VARCHAR(255),
    reason VARCHAR(500),
    evaluated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
