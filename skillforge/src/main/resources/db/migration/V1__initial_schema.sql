CREATE TABLE IF NOT EXISTS `user` (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(254) NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_email (email)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS skill (
    id BIGINT NOT NULL AUTO_INCREMENT,
    skill_name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    category VARCHAR(255) NOT NULL,
    level VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    KEY idx_skill_user_id (user_id),
    KEY idx_skill_category_level (category, level),
    CONSTRAINT fk_skill_user FOREIGN KEY (user_id) REFERENCES `user` (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS refresh_token (
    id BIGINT NOT NULL AUTO_INCREMENT,
    token VARCHAR(255) NOT NULL,
    expiry_date DATETIME(6) NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_refresh_token_token (token),
    UNIQUE KEY uk_refresh_token_user (user_id),
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES `user` (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS skill_exchanges (
    id BIGINT NOT NULL AUTO_INCREMENT,
    requester_id BIGINT NOT NULL,
    skill_owner_id BIGINT NOT NULL,
    offered_skill_id BIGINT NOT NULL,
    requested_skill_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_exchange_requester (requester_id),
    KEY idx_exchange_owner (skill_owner_id),
    CONSTRAINT fk_exchange_requester FOREIGN KEY (requester_id) REFERENCES `user` (id),
    CONSTRAINT fk_exchange_owner FOREIGN KEY (skill_owner_id) REFERENCES `user` (id),
    CONSTRAINT fk_exchange_offered_skill FOREIGN KEY (offered_skill_id) REFERENCES skill (id),
    CONSTRAINT fk_exchange_requested_skill FOREIGN KEY (requested_skill_id) REFERENCES skill (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS rating (
    id BIGINT NOT NULL AUTO_INCREMENT,
    exchange_id BIGINT NOT NULL,
    rater_id BIGINT NOT NULL,
    rated_user_id BIGINT NOT NULL,
    score INT NOT NULL,
    comment VARCHAR(1000),
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_rating_exchange_rater UNIQUE (exchange_id, rater_id),
    CONSTRAINT chk_rating_score CHECK (score BETWEEN 1 AND 5),
    KEY idx_rating_rated_user (rated_user_id),
    CONSTRAINT fk_rating_exchange FOREIGN KEY (exchange_id) REFERENCES skill_exchanges (id),
    CONSTRAINT fk_rating_rater FOREIGN KEY (rater_id) REFERENCES `user` (id),
    CONSTRAINT fk_rating_rated_user FOREIGN KEY (rated_user_id) REFERENCES `user` (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS notification (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    type VARCHAR(40) NOT NULL,
    message VARCHAR(500) NOT NULL,
    is_read BIT(1) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_notification_user_created (user_id, created_at),
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES `user` (id)
) ENGINE=InnoDB;
