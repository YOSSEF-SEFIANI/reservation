package com.skypay.hotel.service.impl;


import com.skypay.hotel.entity.Booking;
import com.skypay.hotel.exception.InvalidDateException;
import com.skypay.hotel.model.BookingCreationData;
import com.skypay.hotel.service.BookingService;
import com.skypay.hotel.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of BookingService
 */
@Slf4j
@Getter
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final List<Booking> bookings = new CopyOnWriteArrayList<>();
    private final UserService userService;

    private final AtomicInteger nextBookingId = new AtomicInteger(1);

    @Override
    public Booking createBooking(BookingCreationData bookingData) {
        log.debug("Creating booking with data: {}", bookingData);

        Booking booking = Booking.builder()
                .id(nextBookingId.getAndIncrement())
                .userId(bookingData.userId())
                .roomNumber(bookingData.roomNumber())
                .roomType(bookingData.roomType())
                .pricePerNight(bookingData.pricePerNight())
                .checkIn(bookingData.checkIn())
                .checkOut(bookingData.checkOut())
                .numberOfNights(bookingData.numberOfNights())
                .totalCost(bookingData.totalCost())
                .createdDate(LocalDateTime.now())
                .build();

        bookings.add(booking);
        log.info("Booking created - ID: {}, User: {}, Room: {}, Total: {}",
                booking.getId(), bookingData.userId(), bookingData.roomNumber(), bookingData.totalCost());

        return booking;
    }

    @Override
    public boolean isRoomAvailable(int roomNumber, LocalDate checkIn, LocalDate checkOut) {
        return bookings.stream()
                .filter(b -> b.getRoomNumber().equals(roomNumber))
                .noneMatch(b -> hasOverlap(b.getCheckIn(), b.getCheckOut(), checkIn, checkOut));
    }

    @Override
    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings);
    }

    @Override
    public void printAllBookings() {
        log.info("printAllBookings called");

        String separator = "=".repeat(80);
        System.out.println("\n" + separator);
        System.out.println("BOOKINGS (Latest to Oldest)");
        System.out.println(separator);

        bookings.stream()
                .sorted(Comparator.comparing(Booking::getCreatedDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .forEach(this::printBooking);

        System.out.println(separator + "\n");
    }

    @Override
    public void validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new InvalidDateException("Les dates de check-in et check-out ne peuvent pas être nulles");
        }
        if (checkIn.isBefore(LocalDate.now())) {
            throw new InvalidDateException(
                    checkIn, checkOut, "La date de check-in ne peut pas être dans le passé");
        }
        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            throw new InvalidDateException(
                    checkIn, checkOut, "La date de check-out doit être après la date de check-in");
        }
    }

    @Override
    public int calculateNumberOfNights(LocalDate checkIn, LocalDate checkOut) {
        return (int) ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    @Override
    public int calculateTotalCost(int pricePerNight, int numberOfNights) {
        return pricePerNight * numberOfNights;
    }

    // ========== Private Helper Methods ==========

    private boolean hasOverlap(LocalDate existingCheckIn, LocalDate existingCheckOut,
                               LocalDate newCheckIn, LocalDate newCheckOut) {
        return !existingCheckOut.isBefore(newCheckIn) && !newCheckOut.isBefore(existingCheckIn);
    }

    private void printBooking(Booking booking) {
        userService.findUserById(booking.getUserId()).ifPresent(user -> {
            System.out.printf(
                    "Booking #%-3d | User: %-5d (Balance: %-8d) | Room: %-5d (%-10s, %-6d/night) | %s to %s (%d nights) | Total: %-6d | Created: %s%n",
                    booking.getId(),
                    booking.getUserId(),
                    user.getBalance(),
                    booking.getRoomNumber(),
                    booking.getRoomType(),
                    booking.getPricePerNight(),
                    booking.getCheckIn(),
                    booking.getCheckOut(),
                    booking.getNumberOfNights(),
                    booking.getTotalCost(),
                    booking.getCreatedDate()
            );
        });
    }

}