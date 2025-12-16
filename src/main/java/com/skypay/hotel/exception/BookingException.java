package com.skypay.hotel.exception;

public sealed class BookingException extends RuntimeException
        permits InvalidDateException, InsufficientBalanceException,
        RoomNotAvailableException, EntityNotFoundException {

    public BookingException(String message) {
        super(message);
    }
}