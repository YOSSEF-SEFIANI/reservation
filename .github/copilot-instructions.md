# Skypay Hotel Reservation System - AI Coding Guide

## Project Overview

Spring Boot 3.5.8 hotel booking system (Java 21) with in-memory data storage. Built for Skypay technical test demonstrating clean architecture and SOLID principles.

## Architecture Patterns

### Service Layer Orchestration

**HotelService** coordinates three domain services without containing business logic:

- `RoomService` - manages room inventory
- `UserService` - handles user accounts and balances
- `BookingService` - orchestrates reservations with availability checks

Implementation uses constructor DI for testing but provides no-arg constructor for main application:

```java
public HotelServiceImpl() {
    this.roomService = new RoomServiceImpl();
    this.userService = new UserServiceImpl();
    this.bookingService = new BookingServiceImpl(userService);
}
```

### Data Snapshot Pattern

**Critical**: Bookings store immutable snapshots of room data to prevent retroactive price changes:

```java
// BookingCreationData captures room state at booking time
BookingCreationData.builder()
    .roomType(room.getType())              // Snapshot
    .pricePerNight(room.getRoomPricePerNight()) // Snapshot
```

Never reference live Room entities from Booking entities.

### Entity Hierarchy

All entities extend `NumericAuditable<Integer>` (from `entity/domain/`):

- Provides `id`, `createdDate`, `lastModifiedDate`, `createdBy`, `lastModifiedBy`
- Use `@SuperBuilder(toBuilder = true)` for builder inheritance
- Override `toString/equals/hashCode` with `@Data`, `@EqualsAndHashCode(callSuper = true)`

### Exception Model

Sealed exception hierarchy under `BookingException`:

```java
public sealed class BookingException extends RuntimeException
    permits InvalidDateException, InsufficientBalanceException,
            RoomNotAvailableException, EntityNotFoundException
```

Use specific subtypes for domain errors; plain `IllegalArgumentException` for infrastructure failures.

## Conventions

### Service Implementation Pattern

1. Store entities in `ArrayList<T>` with package-private getters
2. Implement business logic validation in orchestrating service (HotelService)
3. Delegate pure CRUD to domain services
4. Always use `Optional<T>` for find operations:

```java
Optional<User> findUserById(int userId);
```

### Date/Time Handling

- Use `LocalDate` for booking periods (check-in/check-out)
- Use `LocalDateTime` for audit timestamps (createdDate/lastModifiedDate)
- Calculate nights with `ChronoUnit.DAYS.between(checkIn, checkOut)`

### Validation Flow

For bookings, validate in this order:

1. Date validity (`validateDates()` in BookingService)
2. Entity existence (user/room lookup)
3. Cost calculation
4. Balance sufficiency
5. Room availability
6. State mutation (create booking, deduct balance)

### Logging Strategy

- Use SLF4J via Lombok `@Slf4j`
- `log.debug()` for service method entry with parameters
- `log.info()` for state changes (created/updated entities)
- Never log sensitive data or full entity dumps

## Build & Run Commands

```bash
# Build with Maven wrapper
./mvnw clean install

# Run tests
./mvnw test

# Start application
./mvnw spring-boot:run

# Check dependencies
./mvnw versions:display-dependency-updates
```

## Testing Approach

- Target: 100% test coverage (per README goals)
- Use JUnit 5 + Mockito + AssertJ (provided by `spring-boot-starter-test`)
- Mock sub-services in HotelServiceImpl tests via constructor injection
- Test domain services in isolation with in-memory state

## Key Files

- [HotelServiceImpl.java](src/main/java/com/skypay/hotel/service/impl/HotelServiceImpl.java) - Main orchestration logic
- [BookingCreationData.java](src/main/java/com/skypay/hotel/model/BookingCreationData.java) - Immutable booking snapshot record
- [AbstractAuditable.java](src/main/java/com/skypay/hotel/entity/domain/AbstractAuditable.java) - Base entity with audit fields
- [application.yml](src/main/resources/application.yml) - Disables JPA/Liquibase (in-memory only)
