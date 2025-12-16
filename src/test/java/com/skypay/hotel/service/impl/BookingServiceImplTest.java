package com.skypay.hotel.service.impl;

import com.skypay.hotel.entity.Booking;
import com.skypay.hotel.entity.RoomType;
import com.skypay.hotel.entity.User;
import com.skypay.hotel.exception.InvalidDateException;
import com.skypay.hotel.model.BookingCreationData;
import com.skypay.hotel.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookingService Tests")
class BookingServiceImplTest {

    @Mock
    private UserService userService;

    private BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(userService);
    }

    @Test
    @DisplayName("Should create booking with correct data")
    void shouldCreateBookingWithCorrectData() {
        // Given
        BookingCreationData data = BookingCreationData.builder()
                .userId(1)
                .roomNumber(1)
                .roomType(RoomType.STANDARD)
                .pricePerNight(1000)
                .checkIn(LocalDate.of(2026, 7, 7))
                .checkOut(LocalDate.of(2026, 7, 9))
                .numberOfNights(2)
                .totalCost(2000)
                .build();

        // When
        Booking booking = bookingService.createBooking(data);

        // Then
        assertThat(booking).isNotNull();
        assertThat(booking.getId()).isEqualTo(1);
        assertThat(booking.getUserId()).isEqualTo(1);
        assertThat(booking.getRoomNumber()).isEqualTo(1);
        assertThat(booking.getRoomType()).isEqualTo(RoomType.STANDARD);
        assertThat(booking.getPricePerNight()).isEqualTo(1000);
        assertThat(booking.getCheckIn()).isEqualTo(LocalDate.of(2026, 7, 7));
        assertThat(booking.getCheckOut()).isEqualTo(LocalDate.of(2026, 7, 9));
        assertThat(booking.getNumberOfNights()).isEqualTo(2);
        assertThat(booking.getTotalCost()).isEqualTo(2000);
        assertThat(booking.getCreatedDate()).isNotNull();
    }

    @Test
    @DisplayName("Should increment booking ID for each new booking")
    void shouldIncrementBookingIdForEachNewBooking() {
        // Given
        BookingCreationData data1 = createBookingData(1, 1, LocalDate.of(2026, 7, 7), LocalDate.of(2026, 7, 9));
        BookingCreationData data2 = createBookingData(2, 2, LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 12));

        // When
        Booking booking1 = bookingService.createBooking(data1);
        Booking booking2 = bookingService.createBooking(data2);

        // Then
        assertThat(booking1.getId()).isEqualTo(1);
        assertThat(booking2.getId()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should return true when room is available")
    void shouldReturnTrueWhenRoomIsAvailable() {
        // Given
        LocalDate checkIn = LocalDate.of(2026, 7, 7);
        LocalDate checkOut = LocalDate.of(2026, 7, 9);

        // When
        boolean isAvailable = bookingService.isRoomAvailable(1, checkIn, checkOut);

        // Then
        assertThat(isAvailable).isTrue();
    }

    @Test
    @DisplayName("Should return false when room has overlapping booking")
    void shouldReturnFalseWhenRoomHasOverlappingBooking() {
        // Given
        BookingCreationData existingBooking = createBookingData(
                1, 1,
                LocalDate.of(2026, 7, 7),
                LocalDate.of(2026, 7, 10)
        );
        bookingService.createBooking(existingBooking);

        // When - overlap scenarios
        boolean overlap1 = bookingService.isRoomAvailable(1,
                LocalDate.of(2026, 7, 6),
                LocalDate.of(2026, 7, 8)); // Starts before, ends during

        boolean overlap2 = bookingService.isRoomAvailable(1,
                LocalDate.of(2026, 7, 8),
                LocalDate.of(2026, 7, 11)); // Starts during, ends after

        boolean overlap3 = bookingService.isRoomAvailable(1,
                LocalDate.of(2026, 7, 7),
                LocalDate.of(2026, 7, 10)); // Exact same dates

        boolean overlap4 = bookingService.isRoomAvailable(1,
                LocalDate.of(2026, 7, 8),
                LocalDate.of(2026, 7, 9)); // Completely within

        // Then
        assertThat(overlap1).isFalse();
        assertThat(overlap2).isFalse();
        assertThat(overlap3).isFalse();
        assertThat(overlap4).isFalse();
    }

    @Test
    @DisplayName("Should return true for non-overlapping booking")
    void shouldReturnTrueForNonOverlappingBooking() {
        // Given
        BookingCreationData existingBooking = createBookingData(
                1, 1,
                LocalDate.of(2026, 7, 7),
                LocalDate.of(2026, 7, 10)
        );
        bookingService.createBooking(existingBooking);

        // When - completely separate date ranges
        boolean afterBooking = bookingService.isRoomAvailable(1,
                LocalDate.of(2026, 7, 11),
                LocalDate.of(2026, 7, 15)); // Starts after existing ends

        boolean completelyBefore = bookingService.isRoomAvailable(1,
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 6)); // Ends before existing starts

        // Then
        assertThat(afterBooking).isTrue();
        assertThat(completelyBefore).isTrue();
    }

    @Test
    @DisplayName("Should allow same room for different date ranges")
    void shouldAllowSameRoomForDifferentDateRanges() {
        // Given
        BookingCreationData booking1 = createBookingData(
                1, 1,
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 5)
        );
        bookingService.createBooking(booking1);

        // When
        boolean isAvailable = bookingService.isRoomAvailable(1,
                LocalDate.of(2026, 7, 10),
                LocalDate.of(2026, 7, 15));

        // Then
        assertThat(isAvailable).isTrue();
    }

    @Test
    @DisplayName("Should return all bookings")
    void shouldReturnAllBookings() {
        // Given
        bookingService.createBooking(createBookingData(1, 1, LocalDate.of(2026, 7, 7), LocalDate.of(2026, 7, 9)));
        bookingService.createBooking(createBookingData(2, 2, LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 12)));

        // When
        List<Booking> bookings = bookingService.getAllBookings();

        // Then
        assertThat(bookings).hasSize(2);
    }

    @Test
    @DisplayName("Should validate dates successfully for valid range")
    void shouldValidateDatesSuccessfullyForValidRange() {
        // Given
        LocalDate checkIn = LocalDate.of(2026, 7, 7);
        LocalDate checkOut = LocalDate.of(2026, 7, 9);

        // When & Then - should not throw exception
        bookingService.validateDates(checkIn, checkOut);
    }

    @Test
    @DisplayName("Should throw exception when check-in is null")
    void shouldThrowExceptionWhenCheckInIsNull() {
        // Given
        LocalDate checkOut = LocalDate.of(2026, 7, 9);

        // When & Then
        assertThatThrownBy(() -> bookingService.validateDates(null, checkOut))
                .isInstanceOf(InvalidDateException.class)
                .hasMessageContaining("dates");
    }

    @Test
    @DisplayName("Should throw exception when check-out is null")
    void shouldThrowExceptionWhenCheckOutIsNull() {
        // Given
        LocalDate checkIn = LocalDate.of(2026, 7, 7);

        // When & Then
        assertThatThrownBy(() -> bookingService.validateDates(checkIn, null))
                .isInstanceOf(InvalidDateException.class)
                .hasMessageContaining("dates");
    }

    @Test
    @DisplayName("Should throw exception when check-out is before check-in")
    void shouldThrowExceptionWhenCheckOutIsBeforeCheckIn() {
        // Given
        LocalDate checkIn = LocalDate.of(2026, 7, 9);
        LocalDate checkOut = LocalDate.of(2026, 7, 7);

        // When & Then
        assertThatThrownBy(() -> bookingService.validateDates(checkIn, checkOut))
                .isInstanceOf(InvalidDateException.class)
                .hasMessageContaining("check-out")
                .hasMessageContaining("check-in");
    }

    @Test
    @DisplayName("Should throw exception when check-out equals check-in")
    void shouldThrowExceptionWhenCheckOutEqualsCheckIn() {
        // Given
        LocalDate checkIn = LocalDate.of(2026, 7, 7);
        LocalDate checkOut = LocalDate.of(2026, 7, 7);

        // When & Then
        assertThatThrownBy(() -> bookingService.validateDates(checkIn, checkOut))
                .isInstanceOf(InvalidDateException.class)
                .hasMessageContaining("check-out")
                .hasMessageContaining("check-in");
    }

    @Test
    @DisplayName("Should throw exception when check-in is in the past")
    void shouldThrowExceptionWhenCheckInIsInThePast() {
        // Given
        LocalDate checkIn = LocalDate.of(2020, 1, 1);
        LocalDate checkOut = LocalDate.of(2020, 1, 5);

        // When & Then
        assertThatThrownBy(() -> bookingService.validateDates(checkIn, checkOut))
                .isInstanceOf(InvalidDateException.class)
                .hasMessageContaining("passé");
    }

    @Test
    @DisplayName("Should calculate number of nights correctly")
    void shouldCalculateNumberOfNightsCorrectly() {
        // Given
        LocalDate checkIn = LocalDate.of(2026, 7, 7);
        LocalDate checkOut = LocalDate.of(2026, 7, 9);

        // When
        int nights = bookingService.calculateNumberOfNights(checkIn, checkOut);

        // Then
        assertThat(nights).isEqualTo(2);
    }

    @Test
    @DisplayName("Should calculate one night correctly")
    void shouldCalculateOneNightCorrectly() {
        // Given
        LocalDate checkIn = LocalDate.of(2026, 7, 7);
        LocalDate checkOut = LocalDate.of(2026, 7, 8);

        // When
        int nights = bookingService.calculateNumberOfNights(checkIn, checkOut);

        // Then
        assertThat(nights).isEqualTo(1);
    }

    @Test
    @DisplayName("Should calculate total cost correctly")
    void shouldCalculateTotalCostCorrectly() {
        // When
        int totalCost = bookingService.calculateTotalCost(1000, 3);

        // Then
        assertThat(totalCost).isEqualTo(3000);
    }

    @Test
    @DisplayName("Should print all bookings without errors")
    void shouldPrintAllBookingsWithoutErrors() {
        // Given
        User user = User.builder().id(1).balance(5000).build();
        when(userService.findUserById(1)).thenReturn(Optional.of(user));

        bookingService.createBooking(createBookingData(1, 1, LocalDate.of(2026, 7, 7), LocalDate.of(2026, 7, 9)));

        // When & Then - should not throw exception
        bookingService.printAllBookings();
    }

    private BookingCreationData createBookingData(int userId, int roomNumber, LocalDate checkIn, LocalDate checkOut) {
        int nights = (int) java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
        int pricePerNight = 1000;
        return BookingCreationData.builder()
                .userId(userId)
                .roomNumber(roomNumber)
                .roomType(RoomType.STANDARD)
                .pricePerNight(pricePerNight)
                .checkIn(checkIn)
                .checkOut(checkOut)
                .numberOfNights(nights)
                .totalCost(pricePerNight * nights)
                .build();
    }
}
