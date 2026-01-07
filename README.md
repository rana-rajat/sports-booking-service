# Sports Venue Availability & Booking Service

A dockerized Spring Boot backend service for managing sports venues, time slots, and bookings with availability checks and conflict prevention. This system simulates a real-world sports ground/turf booking platform.

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Features](#features)
- [Setup & Deployment](#setup--deployment)
- [API Documentation](#api-documentation)
- [Architecture](#architecture)
- [Concurrency & Safety](#concurrency--safety)
- [Development](#development)
- [Testing](#testing)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

## Overview

This project provides a complete sports venue booking system with:
- **Venue Management** - Create and manage sports venues
- **Slot Management** - Schedule time slots with automatic overlap detection
- **Availability Checking** - Real-time venue availability based on time and sport
- **Booking System** - Secure booking with double-booking prevention
- **Concurrency Safety** - Pessimistic locking and transaction management

## Tech Stack

- **Java 17** - Spring Boot 3.5.9
- **MySQL 8.0** - Relational database with optimized schema
- **Docker & Docker Compose** - Containerization and orchestration
- **Maven** - Build and dependency management
- **REST APIs** - JSON-based communication
- **Hibernate JPA** - Object-relational mapping
- **Postman** - API testing and documentation

## Features

✅ **Venue Management**
- Create, list, view, and delete venues
- Support for multiple sports per venue

✅ **Slot Management**
- Add time slots with automatic overlap detection
- No concurrent slots for same time range
- Flexible slot status tracking

✅ **Availability API**
- Find available venues for specific time range and sport
- Real-time availability checks

✅ **Booking System**
- Create, view, and cancel bookings
- Booking status tracking (CONFIRMED, CANCELLED)

✅ **Double-Booking Protection**
- Pessimistic locking mechanism
- Status validation before booking
- Atomic operations for data consistency

✅ **Sport Management**
- Dynamic sport syncing from external API (https://stapubox.com/sportslist/)
- No hardcoded sports data

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/venues` | Create new venue |
| GET | `/venues` | List all venues |
| GET | `/venues/{id}` | Get venue by ID |
| DELETE | `/venues/{id}` | Delete venue |
| POST | `/venues/{venueId}/slots` | Add time slot to venue |
| GET | `/venues/available` | Find available venues (query params: startTime, endTime, sport) |
| POST | `/bookings` | Create new booking |
| GET | `/bookings` | List all bookings |
| GET | `/bookings/{id}` | Get booking by ID |
| PUT | `/bookings/{id}/cancel` | Cancel booking |

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    REST API LAYER                           │
│  VenueController | SlotController | BookingController      │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│                    SERVICE LAYER                            │
│  VenueService | SlotService | BookingService |             │
│  AvailabilityService | SportSyncService                     │
│                                                             │
│  ✅ Business Logic  ✅ Validation  ✅ Concurrency Safety  │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│                REPOSITORY LAYER (Data Access)              │
│  Custom JPA queries | Pessimistic locking | Transactions   │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│                  MYSQL DATABASE                            │
│  Sport | Venue | Slot | Booking                           │
│  ✅ 11 Indexes  ✅ FK Constraints  ✅ Unique Constraints  │
└─────────────────────────────────────────────────────────────┘
```

### Project Structure

```
sports-booking/
├── src/
│   ├── main/
│   │   ├── java/com/example/sports_booking/
│   │   │   ├── SportsBookingApplication.java
│   │   │   ├── controller/
│   │   │   │   ├── BookingController.java
│   │   │   │   ├── SlotController.java
│   │   │   │   └── VenueController.java
│   │   │   ├── service/
│   │   │   │   ├── AvailabilityService.java
│   │   │   │   ├── BookingService.java
│   │   │   │   ├── SlotService.java
│   │   │   │   ├── SportSyncService.java
│   │   │   │   └── VenueService.java
│   │   │   ├── repository/
│   │   │   │   ├── BookingRepository.java
│   │   │   │   ├── SlotRepository.java
│   │   │   │   ├── SportRepository.java
│   │   │   │   └── VenueRepository.java
│   │   │   ├── entity/
│   │   │   │   ├── Booking.java
│   │   │   │   ├── BookingStatus.java
│   │   │   │   ├── Slot.java
│   │   │   │   ├── SlotStatus.java
│   │   │   │   ├── Sport.java
│   │   │   │   └── Venue.java
│   │   │   ├── dto/
│   │   │   │   ├── AvailableVenueDTO.java
│   │   │   │   ├── BookingDTO.java
│   │   │   │   ├── CreateBookingRequest.java
│   │   │   │   ├── CreateSlotRequest.java
│   │   │   │   ├── CreateVenueRequest.java
│   │   │   │   ├── SlotDTO.java
│   │   │   │   └── VenueDTO.java
│   │   │   └── exception/
│   │   │       ├── BookingException.java
│   │   │       ├── GlobalExceptionHandler.java
│   │   │       ├── ResourceNotFoundException.java
│   │   │       └── SlotOverlapException.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/com/example/sports_booking/
│           └── SportsBookingApplicationTests.java
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── init.sql
└── README.md
```

## Concurrency & Safety

### Double Booking Prevention
- **Pessimistic WRITE locking** on slot during booking transaction
- **Double-check pattern** in BookingService.book()
- **@Transactional annotation** ensures ACID compliance
- **Atomic check-and-set** operation on slot status

### Slot Overlap Prevention
- Pre-insert overlap query check
- Database UNIQUE constraint: (venue_id, start_time, end_time)
- Application-level validation with SlotOverlapException
- Impossible to create overlapping slots

### Data Consistency
- ACID compliance via @Transactional
- Foreign key constraints with cascade options
- Timestamp tracking for audit trail
- Complete data integrity

## Setup & Deployment

### Prerequisites

- **Docker** version 20.10+
- **Docker Compose** version 1.29+
- **Git** (for repository cloning)
- Ports 8080 (application) and 3306 (MySQL) must be available
- Minimum 2GB RAM allocated to Docker

### Quick Start with Docker Compose

```bash
# 1. Clone the repository
git clone <repository-url>
cd sports-booking

# 2. Start all services
docker-compose up -d

# 3. Verify services are running
docker-compose ps

# 4. Check application logs
docker-compose logs -f app

# 5. Test the API
curl http://localhost:8080/venues

# 6. Stop services
docker-compose down

# 7. Stop and remove all volumes (clean slate)
docker-compose down -v
```

### Local Development

```bash
# Build the project locally
mvn clean package

# Run the application
java -jar target/sports-booking-0.0.1-SNAPSHOT.jar

# Run unit tests
mvn test

# Run tests with coverage
mvn clean test jacoco:report
```

### Postman Collection

A complete Postman collection is available in `postman-collection.json` with:
- All API endpoints pre-configured
- Request/response examples
- Environment variables for easy switching
- Pre-request scripts for validation

**To Import:**
1. Open Postman
2. Click **Import**
3. Select `postman-collection.json` from the project root
4. Use the **local** environment for testing
5. Start making requests

## Development

### Build and Compile

```bash
# Clean and build
mvn clean install

# Build without running tests
mvn clean install -DskipTests

# Build specific module
mvn clean install -pl :sports-booking
```

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=SportsBookingApplicationTests

# Run with detailed output
mvn test -X
```

**Last Updated**: January 7, 2026



