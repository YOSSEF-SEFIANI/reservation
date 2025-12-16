package com.skypay.hotel.exception;

import java.time.LocalDate;

/**
 * Exception levée lorsque les dates de réservation sont invalides.
 */
public final class InvalidDateException extends BookingException {
    
    public InvalidDateException(String message) {
        super(message);
    }
    
    /**
     * Constructeur avec dates.
     */
    public InvalidDateException(LocalDate checkIn, LocalDate checkOut, String reason) {
        super(String.format("%s (Check-in: %s, Check-out: %s)", reason, checkIn, checkOut));
    }
}