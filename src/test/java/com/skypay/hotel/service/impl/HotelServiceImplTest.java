package com.skypay.hotel.service.impl;

import com.skypay.hotel.dto.BookingRequest;
import com.skypay.hotel.entity.Booking;
import com.skypay.hotel.entity.Room;
import com.skypay.hotel.entity.RoomType;
import com.skypay.hotel.entity.User;
import com.skypay.hotel.exception.EntityNotFoundException;
import com.skypay.hotel.exception.InsufficientBalanceException;
import com.skypay.hotel.exception.RoomNotAvailableException;
import com.skypay.hotel.model.BookingCreationData;
import com.skypay.hotel.service.BookingService;
import com.skypay.hotel.service.RoomService;
import com.skypay.hotel.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HotelService Tests")
class HotelServiceImplTest {

    @Mock
    private RoomService roomService;

    @Mock
    private UserService userService;

    @Mock
    private BookingService bookingService;

    private HotelServiceImpl hotelService;

    @BeforeEach
    void setUp() {
        hotelService = new HotelServiceImpl(roomService, userService, bookingService);
    }

    @Test
    @DisplayName("Should delegate setRoom to RoomService")
    void shouldDelegateSetRoomToRoomService() {
        // Given
        Room room = Room.builder()
                .id(1)
                .type(RoomType.STANDARD)
                .roomPricePerNight(1000)
                .build();

        // When
        hotelService.setRoom(room);

        // Then
        verify(roomService).setRoom(1, RoomType.STANDARD, 1000);
    }

    @Test
    @DisplayName("Should delegate setUser to UserService")
    void shouldDelegateSetUserToUserService() {
        // Given
        User user = User.builder()
                .id(1)
                .balance(5000)
                .build();

        // When
        hotelService.setUser(user);

        // Then
        verify(userService).setUser(1, 5000);
    }

    @Test
    @DisplayName("Should successfully book room when all conditions are met")
    void shouldSuccessfullyBookRoomWhenAllConditionsMet() {
        // Given
        LocalDate checkIn = LocalDate.of(2026, 7, 7);
        LocalDate checkOut = LocalDate.of(2026, 7, 9);
        BookingRequest request = BookingRequest.builder()
                .userId(1)
                .roomNumber(1)
                .checkIn(checkIn)
                .checkOut(checkOut)
                .build();

        User user = User.builder().id(1).balance(5000).build();
        Room room = Room.builder()
                .id(1)
                .type(RoomType.STANDARD)
                .roomPricePerNight(1000)
                .build();

        when(userService.findUserById(1)).thenReturn(Optional.of(user));
        when(roomService.findRoomByNumber(1)).thenReturn(Optional.of(room));
        when(bookingService.calculateNumberOfNights(checkIn, checkOut)).thenReturn(2);
        when(bookingService.calculateTotalCost(1000, 2)).thenReturn(2000);
        when(userService.hasSufficientBalance(1, 2000)).thenReturn(true);
        when(bookingService.isRoomAvailable(1, checkIn, checkOut)).thenReturn(true);

        // When
        hotelService.bookRoom(request);

        // Then
        verify(bookingService).validateDates(checkIn, checkOut);
        verify(userService).findUserById(1);
        verify(roomService).findRoomByNumber(1);
        verify(bookingService).calculateNumberOfNights(checkIn, checkOut);
        verify(bookingService).calculateTotalCost(1000, 2);
        verify(userService).hasSufficientBalance(1, 2000);
        verify(bookingService).isRoomAvailable(1, checkIn, checkOut);

        ArgumentCaptor<BookingCreationData> captor = ArgumentCaptor.forClass(BookingCreationData.class);
        verify(bookingService).createBooking(captor.capture());

        BookingCreationData captured = captor.getValue();
        assertThat(captured.userId()).isEqualTo(1);
        assertThat(captured.roomNumber()).isEqualTo(1);
        assertThat(captured.roomType()).isEqualTo(RoomType.STANDARD);
        assertThat(captured.pricePerNight()).isEqualTo(1000);
        assertThat(captured.numberOfNights()).isEqualTo(2);
        assertThat(captured.totalCost()).isEqualTo(2000);

        verify(userService).deductBalance(1, 2000);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        BookingRequest request = BookingRequest.builder()
                .userId(999)
                .roomNumber(1)
                .checkIn(LocalDate.of(2026, 7, 7))
                .checkOut(LocalDate.of(2026, 7, 9))
                .build();

        when(userService.findUserById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> hotelService.bookRoom(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User")
                .hasMessageContaining("999");

        verify(bookingService).validateDates(any(), any());
        verify(userService).findUserById(999);
        verify(roomService, never()).findRoomByNumber(anyInt());
        verify(bookingService, never()).createBooking(any());
    }

    @Test
    @DisplayName("Should throw exception when room not found")
    void shouldThrowExceptionWhenRoomNotFound() {
        // Given
        BookingRequest request = BookingRequest.builder()
                .userId(1)
                .roomNumber(999)
                .checkIn(LocalDate.of(2026, 7, 7))
                .checkOut(LocalDate.of(2026, 7, 9))
                .build();

        User user = User.builder().id(1).balance(5000).build();

        when(userService.findUserById(1)).thenReturn(Optional.of(user));
        when(roomService.findRoomByNumber(999)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> hotelService.bookRoom(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Room")
                .hasMessageContaining("999");

        verify(bookingService).validateDates(any(), any());
        verify(userService).findUserById(1);
        verify(roomService).findRoomByNumber(999);
        verify(bookingService, never()).createBooking(any());
    }

    @Test
    @DisplayName("Should throw exception when insufficient balance")
    void shouldThrowExceptionWhenInsufficientBalance() {
        // Given
        LocalDate checkIn = LocalDate.of(2026, 7, 7);
        LocalDate checkOut = LocalDate.of(2026, 7, 9);
        BookingRequest request = BookingRequest.builder()
                .userId(1)
                .roomNumber(1)
                .checkIn(checkIn)
                .checkOut(checkOut)
                .build();

        User user = User.builder().id(1).balance(1000).build();
        Room room = Room.builder()
                .id(1)
                .type(RoomType.SUITE)
                .roomPricePerNight(3000)
                .build();

        when(userService.findUserById(1)).thenReturn(Optional.of(user));
        when(roomService.findRoomByNumber(1)).thenReturn(Optional.of(room));
        when(bookingService.calculateNumberOfNights(checkIn, checkOut)).thenReturn(2);
        when(bookingService.calculateTotalCost(3000, 2)).thenReturn(6000);
        when(userService.hasSufficientBalance(1, 6000)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> hotelService.bookRoom(request))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessageContaining("Solde insuffisant");

        verify(bookingService, never()).isRoomAvailable(anyInt(), any(), any());
        verify(bookingService, never()).createBooking(any());
        verify(userService, never()).deductBalance(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Should throw exception when room not available")
    void shouldThrowExceptionWhenRoomNotAvailable() {
        // Given
        LocalDate checkIn = LocalDate.of(2026, 7, 7);
        LocalDate checkOut = LocalDate.of(2026, 7, 9);
        BookingRequest request = BookingRequest.builder()
                .userId(1)
                .roomNumber(1)
                .checkIn(checkIn)
                .checkOut(checkOut)
                .build();

        User user = User.builder().id(1).balance(5000).build();
        Room room = Room.builder()
                .id(1)
                .type(RoomType.STANDARD)
                .roomPricePerNight(1000)
                .build();

        when(userService.findUserById(1)).thenReturn(Optional.of(user));
        when(roomService.findRoomByNumber(1)).thenReturn(Optional.of(room));
        when(bookingService.calculateNumberOfNights(checkIn, checkOut)).thenReturn(2);
        when(bookingService.calculateTotalCost(1000, 2)).thenReturn(2000);
        when(userService.hasSufficientBalance(1, 2000)).thenReturn(true);
        when(bookingService.isRoomAvailable(1, checkIn, checkOut)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> hotelService.bookRoom(request))
                .isInstanceOf(RoomNotAvailableException.class)
                .hasMessageContaining("chambre")
                .hasMessageContaining("disponible");

        verify(bookingService, never()).createBooking(any());
        verify(userService, never()).deductBalance(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Should delegate printAll to sub-services")
    void shouldDelegatePrintAllToSubServices() {
        // When
        hotelService.printAll();

        // Then
        verify(roomService).printAllRooms();
        verify(bookingService).printAllBookings();
    }

    @Test
    @DisplayName("Should delegate printAllUsers to UserService")
    void shouldDelegatePrintAllUsersToUserService() {
        // When
        hotelService.printAllUsers();

        // Then
        verify(userService).printAllUsers();
    }
}
