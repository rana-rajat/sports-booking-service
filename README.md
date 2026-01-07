# Sports Venue Availability & Booking Service

A dockerized Spring Boot backend service for managing sports venues, time slots, and bookings with availability checks and conflict prevention. This system simulates a real-world sports ground/turf booking platform.

## Tech Stack

- **Java 17** - Spring Boot 3.5.9
- **MySQL 8.0** - Relational database
- **Docker & Docker Compose** - Containerization
- **Maven** - Build tool
- **REST APIs** - JSON-based communication
- **Hibernate JPA** - ORM framework
- **Lombok** - Code generation

## Core Features

1. **Venue Management** - Create, list, view, and delete sports venues
2. **Slot Management** - Add time slots per venue with overlap prevention
3. **Availability API** - Fetch available venues for a given time range & sport
4. **Booking System** - Book, view, and cancel slots with double-booking prevention
5. **Sport Management** - Sync sports from external API (https://stapubox.com/sportslist/)

## Key Assumptions

1. **One Booking per Slot** - Each booking corresponds to exactly one time slot
2. **Immutable Slot Time** - Once a slot is booked, the time range cannot be modified
3. **Cancelled Booking Frees Slot** - When a booking is cancelled, the slot becomes available immediately
4. **Single Database Instance** - No external caching layer; all state managed in MySQL
5. **Pessimistic Locking** - Concurrent bookings use database locks to prevent race conditions
6. **Sport Sync on Startup** - Sports are fetched from external API during application startup
7. **Timezone Aware** - All timestamps stored in UTC format

## Database Schema

### Indexes for Performance

```
Slot Table:
- idx_venue_id (venue_id)
- idx_status (status)
- idx_venue_time (venue_id, start_time, end_time)
- idx_slot_time_range (start_time, end_time)

Booking Table:
- idx_slot_id (slot_id)
- idx_user_name (user_name)
- idx_booking_status (status)
```

## Concurrency & Safety

**Double Booking Prevention:**
- Pessimistic WRITE locking on slot during booking transaction
- Double-check pattern in BookingService.book()
- @Transactional ensures ACID compliance
- Slot status atomic check-and-set operation

**Conflict Resolution:**
- Unique constraint prevents duplicate slot creation for same time range
- Foreign key constraints maintain referential integrity
- Cascading delete on venue deletion
- Restrict delete on slot with active bookings

## API Endpoints

### Venues

#### Create Venue
```
POST /venues
Content-Type: application/json

{
  "name": "Central Sports Complex",
  "location": "Downtown",
  "sportId": "cricket"
}

Response: 201 Created
{
  "id": 1,
  "name": "Central Sports Complex",
  "location": "Downtown",
  "sportId": "cricket",
  "createdAt": "2024-01-06 10:30:00",
  "updatedAt": "2024-01-06 10:30:00"
}
```

#### Get All Venues
```
GET /venues

Response: 200 OK
[
  {
    "id": 1,
    "name": "Central Sports Complex",
    "location": "Downtown",
    "sportId": "cricket",
    "createdAt": "2024-01-06 10:30:00",
    "updatedAt": "2024-01-06 10:30:00"
  }
]
```

#### Get Single Venue
```
GET /venues/{venueId}

Response: 200 OK
{
  "id": 1,
  "name": "Central Sports Complex",
  "location": "Downtown",
  "sportId": "cricket",
  "createdAt": "2024-01-06 10:30:00",
  "updatedAt": "2024-01-06 10:30:00"
}
```

#### Delete Venue
```
DELETE /venues/{venueId}

Response: 204 No Content
```

### Slots

#### Add Slot to Venue
```
POST /venues/{venueId}/slots
Content-Type: application/json

{
  "startTime": "2024-01-15 09:00:00",
  "endTime": "2024-01-15 10:00:00"
}

Response: 201 Created
{
  "id": 1,
  "venueId": 1,
  "venueName": "Central Sports Complex",
  "sportId": "cricket",
  "startTime": "2024-01-15 09:00:00",
  "endTime": "2024-01-15 10:00:00",
  "status": "AVAILABLE",
  "createdAt": "2024-01-06 10:30:00",
  "updatedAt": "2024-01-06 10:30:00"
}
```

### Availability

#### Get Available Venues
```
GET /venues/available?startTime=2024-01-15 09:00:00&endTime=2024-01-15 10:00:00&sportId=cricket

Query Parameters:
- startTime (required, format: yyyy-MM-dd HH:mm:ss)
- endTime (required, format: yyyy-MM-dd HH:mm:ss)
- sportId (optional, filters by sport)

Response: 200 OK
[
  {
    "venueId": 1,
    "venueName": "Central Sports Complex",
    "location": "Downtown",
    "sportId": "cricket",
    "slotId": 1,
    "slotStartTime": "2024-01-15 09:00:00",
    "slotEndTime": "2024-01-15 10:00:00"
  }
]
```

### Bookings

#### Create Booking
```
POST /bookings
Content-Type: application/json

{
  "slotId": 1,
  "userName": "John Doe"
}

Response: 201 Created
{
  "id": 1,
  "slotId": 1,
  "venueId": 1,
  "venueName": "Central Sports Complex",
  "userName": "John Doe",
  "slotStartTime": "2024-01-15 09:00:00",
  "slotEndTime": "2024-01-15 10:00:00",
  "status": "CONFIRMED",
  "createdAt": "2024-01-06 10:35:00",
  "cancelledAt": null
}
```

#### Get All Bookings
```
GET /bookings

Response: 200 OK
[
  {
    "id": 1,
    "slotId": 1,
    "venueId": 1,
    "venueName": "Central Sports Complex",
    "userName": "John Doe",
    "slotStartTime": "2024-01-15 09:00:00",
    "slotEndTime": "2024-01-15 10:00:00",
    "status": "CONFIRMED",
    "createdAt": "2024-01-06 10:35:00",
    "cancelledAt": null
  }
]
```

#### Get Single Booking
```
GET /bookings/{bookingId}

Response: 200 OK
{
  "id": 1,
  "slotId": 1,
  "venueId": 1,
  "venueName": "Central Sports Complex",
  "userName": "John Doe",
  "slotStartTime": "2024-01-15 09:00:00",
  "slotEndTime": "2024-01-15 10:00:00",
  "status": "CONFIRMED",
  "createdAt": "2024-01-06 10:35:00",
  "cancelledAt": null
}
```

#### Cancel Booking
```
PUT /bookings/{bookingId}/cancel

Response: 204 No Content
```

## Setup & Deployment

### Prerequisites

- Docker & Docker Compose installed
- Port 8080 (app) and 3306 (MySQL) available
- Git (for cloning)

### Quick Start with Docker Compose

```bash
# Navigate to project directory
cd sports-booking

# Start all services
docker-compose up -d

# Verify services are running
docker-compose ps

# View app logs
docker-compose logs -f app

# Stop services
docker-compose down

# Stop and remove volumes (clean slate)
docker-compose down -v
```

### Access

- **API Base URL**: http://localhost:8080
- **MySQL**: localhost:3306
  - Username: `root`
  - Password: `root`
  - Database: `sports_booking`

### Local Development

```bash
# Build locally
mvn clean package

# Run application
java -jar target/sports-booking-0.0.1-SNAPSHOT.jar

# Run tests
mvn test
```

## Testing

### Sample Test Scenarios

#### Scenario 1: Successful Booking
```bash
# Create venue
curl -X POST http://localhost:8080/venues \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Stadium A",
    "location": "City Center",
    "sportId": "cricket"
  }'

# Add slot
curl -X POST http://localhost:8080/venues/1/slots \
  -H "Content-Type: application/json" \
  -d '{
    "startTime": "2024-01-15 14:00:00",
    "endTime": "2024-01-15 15:00:00"
  }'

# Book slot
curl -X POST http://localhost:8080/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "slotId": 1,
    "userName": "Alice"
  }'

# Expected: 201 Created
```

#### Scenario 2: Double Booking Prevention
```bash
# Try to book same slot again (should fail with 409 Conflict)
curl -X POST http://localhost:8080/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "slotId": 1,
    "userName": "Bob"
  }'

# Expected: 409 Conflict
# "Slot is already booked"
```

#### Scenario 3: Slot Overlap Prevention
```bash
# Try to add overlapping slot (should fail with 409 Conflict)
curl -X POST http://localhost:8080/venues/1/slots \
  -H "Content-Type: application/json" \
  -d '{
    "startTime": "2024-01-15 14:30:00",
    "endTime": "2024-01-15 15:30:00"
  }'

# Expected: 409 Conflict
# "Slot overlaps with existing slots for venue: Stadium A"
```

## Postman Collection

A complete Postman collection is available in `postman-collection.json` with:
- All API endpoints
- Pre-configured request/response examples
- Environment variables for easy switching between local and production
- Test scripts for validation

### Import to Postman

1. Open Postman
2. Click **Import**
3. Select `postman-collection.json`
4. Start using the API



