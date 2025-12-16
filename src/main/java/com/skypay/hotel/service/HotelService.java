package com.skypay.hotel.service;

import com.skypay.hotel.dto.BookingRequest;
import com.skypay.hotel.entity.Room;
import com.skypay.hotel.entity.User;

/**
 * Service interface for Hotel Reservation System
 * Based on technical requirements from Skypay Technical Test 2
 */
public interface HotelService {

    /**
     * Creates a room if it does not already exist.
     * The function setRoom(... ) should not impact the previously created bookings.
     */
    void setRoom(Room room);

    /**
     * Books a room for a user for a specific period.
     * A User can book a room for a specific period if he has enough balance
     * for the specified period and the room is free on that period.
     * If the booking is successful, the user balance is updated.
     */
    void bookRoom(BookingRequest request);

    /**
     * Prints all rooms data and bookings data both from the latest created to the oldest created.
     * The booking data should contain all the information about the room and user when the booking was done.
     */
    void printAll();

    /**
     * Creates a user if it does not already exist.
     */
    void setUser(User user);

    /**
     * Prints all user data from the latest created to the oldest created.
     */
    void printAllUsers();

}