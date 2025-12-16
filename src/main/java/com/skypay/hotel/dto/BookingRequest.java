package com.skypay.hotel.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class BookingRequest {
    Integer userId;
    Integer roomNumber;
    LocalDate checkIn;
    LocalDate checkOut;
}
