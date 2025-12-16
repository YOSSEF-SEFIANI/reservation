package com.skypay.hotel.entity;


import com.skypay.hotel.entity.domain.NumericAuditable;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

/**
 * Booking Entity - Data only
 * Links a User to a Room for a specific period.
 * Stores snapshot of room data to prevent impact from future changes.
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class Booking extends NumericAuditable<Integer> {

    /**
     * Reference to the user who made the booking
     */
    private Integer userId;

    /**
     * Room number that was booked (references Room. getId())
     */
    private Integer roomNumber;

    /**
     * Snapshot:  Room type at the time of booking
     */
    private RoomType roomType;

    /**
     * Snapshot: Price per night at the time of booking
     */
    private int pricePerNight;

    /**
     * Check-in date
     */
    private LocalDate checkIn;

    /**
     * Check-out date
     */
    private LocalDate checkOut;

    /**
     * Total cost of the booking
     */
    private int totalCost;

    /**
     * Number of nights for this booking
     */
    private int numberOfNights;

}