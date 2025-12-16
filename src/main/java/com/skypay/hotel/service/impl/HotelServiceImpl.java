package com.skypay.hotel.service.impl;

import com.skypay.hotel.dto.BookingRequest;
import com.skypay.hotel.entity.Room;
import com.skypay.hotel.entity.User;
import com.skypay.hotel.exception.EntityNotFoundException;
import com.skypay.hotel.exception.InsufficientBalanceException;
import com.skypay.hotel.exception.RoomNotAvailableException;
import com.skypay.hotel.model.BookingCreationData;
import com.skypay.hotel.service.BookingService;
import com.skypay.hotel.service.HotelService;
import com.skypay.hotel.service.RoomService;
import com.skypay.hotel.service.UserService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Main implementation of HotelService
 * Orchestrates Room, User, and Booking services
 */
@Slf4j
@Getter
public class HotelServiceImpl implements HotelService {

    private final RoomService roomService;
    private final UserService userService;
    private final BookingService bookingService;

    public HotelServiceImpl() {
        this.roomService = new RoomServiceImpl();
        this.userService = new UserServiceImpl();
        this.bookingService = new BookingServiceImpl(userService);
        log.info("HotelService initialized with all sub-services");
    }

    // Constructor for dependency injection
    public HotelServiceImpl(RoomService roomService, UserService userService, BookingService bookingService) {
        this.roomService = roomService;
        this.userService = userService;
        this.bookingService = bookingService;
        log.info("HotelService initialized with injected services");
    }

    @Override
    public void setRoom(Room room) {
        log.debug("HotelService.setRoom called - delegating to RoomService");
        roomService.setRoom(room.getId(), room.getType(), room.getRoomPricePerNight());
    }

    @Override
    public void setUser(User user) {
        log.debug("HotelService.setUser called - delegating to UserService");
        userService.setUser(user.getId(), user.getBalance());
    }

    @Override
    public void bookRoom(BookingRequest request) {

        // Validate dates
        bookingService.validateDates(request.getCheckIn(), request.getCheckOut());

        // Find user
        User user = userService.findUserById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User", request.getUserId()));

        // Find room
        Room room = roomService.findRoomByNumber(request.getRoomNumber())
                .orElseThrow(() -> new EntityNotFoundException("Room", request.getRoomNumber()));

        // Calculate cost
        int numberOfNights = bookingService.calculateNumberOfNights(request.getCheckIn(), request.getCheckOut());
        int totalCost = bookingService.calculateTotalCost(room.getRoomPricePerNight(), numberOfNights);

        // Validate balance
        if (!userService.hasSufficientBalance(request.getUserId(), totalCost)) {
            throw new InsufficientBalanceException(totalCost, user.getBalance());
        }

        // Validate room availability
        if (!bookingService.isRoomAvailable(request.getRoomNumber(), request.getCheckIn(), request.getCheckOut())) {
            throw new RoomNotAvailableException(
                    request.getRoomNumber(), request.getCheckIn(), request.getCheckOut());
        }

        BookingCreationData creationData = BookingCreationData.builder()
                .userId(request.getUserId())
                .roomNumber(request.getRoomNumber())
                .roomType(room.getType())              // Snapshot
                .pricePerNight(room.getRoomPricePerNight()) // Snapshot
                .checkIn(request.getCheckIn())
                .checkOut(request.getCheckOut())
                .numberOfNights(numberOfNights)
                .totalCost(totalCost)
                .build();
        // Create booking with snapshot data
        bookingService.createBooking(creationData);

        // Deduct balance
        userService.deductBalance(request.getUserId(), totalCost);

        log.info("Booking completed successfully - User: {}, Room:  {}, Total: {}",
                request.getUserId(), request.getRoomNumber(), totalCost);
    }

    @Override
    public void printAll() {
        log.info("HotelService.printAll called");
        roomService.printAllRooms();
        bookingService.printAllBookings();
    }

    @Override
    public void printAllUsers() {
        log.info("HotelService.printAllUsers called - delegating to UserService");
        userService.printAllUsers();
    }

}