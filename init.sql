-- Database initialization script for Sports Booking System
-- This script runs automatically on first container startup

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS sports_booking CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sports_booking;

-- Create tables with proper constraints and indexes
CREATE TABLE IF NOT EXISTS sport (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sport_id VARCHAR(50) NOT NULL UNIQUE,
    sport_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_sport_id_unique (sport_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS venue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    location VARCHAR(200) NOT NULL,
    sport_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_sport_id (sport_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS slot (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    venue_id BIGINT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_venue_slot_time (venue_id, start_time, end_time),
    CONSTRAINT fk_slot_venue FOREIGN KEY (venue_id) REFERENCES venue(id) ON DELETE CASCADE,
    INDEX idx_venue_id (venue_id),
    INDEX idx_status (status),
    INDEX idx_venue_time (venue_id, start_time, end_time),
    INDEX idx_slot_time_range (start_time, end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS booking (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    slot_id BIGINT NOT NULL,
    user_name VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    cancelled_at TIMESTAMP NULL,
    CONSTRAINT fk_booking_slot FOREIGN KEY (slot_id) REFERENCES slot(id) ON DELETE RESTRICT,
    INDEX idx_slot_id (slot_id),
    INDEX idx_user_name (user_name),
    INDEX idx_booking_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
