package com.skypay.hotel.exception;

import java.time.LocalDate;

/**
 * Exception levée lorsqu'une chambre n'est pas disponible.
 */
public final class RoomNotAvailableException extends BookingException {
    
    public RoomNotAvailableException(String message) {
        super(message);
    }
    
    /**
     * Constructeur avec numéro de chambre et période.
     */
    public RoomNotAvailableException(int roomNumber, LocalDate checkIn, LocalDate checkOut) {
        super(String.format("La chambre %d n'est pas disponible du %s au %s", 
                roomNumber, checkIn, checkOut));
    }
}