-- PostgreSQL initialization script for Puentenet Market Monitor
-- This script creates tables and inserts initial users

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create instruments table
CREATE TABLE IF NOT EXISTS instruments (
    id BIGSERIAL PRIMARY KEY,
    symbol VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    current_price DECIMAL(19,4),
    daily_change DECIMAL(19,4),
    daily_change_percent DECIMAL(19,4),
    day_high DECIMAL(19,4),
    day_low DECIMAL(19,4),
    volume BIGINT,
    last_updated TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- Create favorites table
CREATE TABLE IF NOT EXISTS favorites (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    instrument_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, instrument_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (instrument_id) REFERENCES instruments(id) ON DELETE CASCADE
);

-- Insert initial users into the users table with BCrypt encrypted passwords
-- These passwords are encrypted using BCrypt with strength 10

-- Admin user: admin@puentenet.com / admin123
INSERT INTO users (name, email, password, role, created_at, updated_at) 
VALUES (
    'Administrator',
    'admin@puentenet.com',
    '$2a$10$Rq7FJ57ka3ASiibYp1IXZuyEKCGo939Y3pr7m63T/5sdpQ08IyAjm', -- user123
    'ADMIN',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Regular user: user@puentenet.com / user123
INSERT INTO users (name, email, password, role, created_at, updated_at) 
VALUES (
    'Regular User',
    'user@puentenet.com',
    '$2a$10$Rq7FJ57ka3ASiibYp1IXZuyEKCGo939Y3pr7m63T/5sdpQ08IyAjm', -- user123
    'USER',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
); 