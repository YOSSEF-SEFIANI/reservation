package com.skypay.hotel.model;

import com.skypay.hotel.entity.RoomType;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record BookingCreationData(
        int userId,
        int roomNumber,
        RoomType roomType,
        int pricePerNight,
        LocalDate checkIn,
        LocalDate checkOut,
        int numberOfNights,
        int totalCost
) {}