package com.skypay.hotel.service;

import com.skypay.hotel.entity.Booking;
import com.skypay.hotel.model.BookingCreationData;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for Booking management
 */
public interface BookingService {

    /**
     * Creates a booking
     *

     * @return the created booking
     */
    Booking createBooking(BookingCreationData creationData);

    /**
     * Checks if a room is available for the requested period
     *
     * @param roomNumber the room number
     * @param checkIn    the check-in date
     * @param checkOut   the check-out date
     * @return true if room is available
     */
    boolean isRoomAvailable(int roomNumber, LocalDate checkIn, LocalDate checkOut);

    /**
     * Gets all bookings
     *
     * @return list of all bookings
     */
    List<Booking> getAllBookings();

    /**
     * Prints all bookings from latest to oldest created
     */
    void printAllBookings();

    /**
     * Validates booking dates
     *
     * @param checkIn  the check-in date
     * @param checkOut the check-out date
     * @throws IllegalArgumentException if dates are invalid
     */
    void validateDates(LocalDate checkIn, LocalDate checkOut);

    /**
     * Calculates number of nights between check-in and check-out
     *
     * @param checkIn  the check-in date
     * @param checkOut the check-out date
     * @return number of nights
     */
    int calculateNumberOfNights(LocalDate checkIn, LocalDate checkOut);

    /**
     * Calculates total cost for a booking
     *
     * @param pricePerNight  the price per night
     * @param numberOfNights the number of nights
     * @return total cost
     */
    int calculateTotalCost(int pricePerNight, int numberOfNights);

}