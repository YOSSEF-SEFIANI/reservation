package com.skypay.hotel.exception;

/**
 * Exception levée lorsque le solde de l'utilisateur est insuffisant.
 */
public final class InsufficientBalanceException extends BookingException {
    
    public InsufficientBalanceException(String message) {
        super(message);
    }
    
    /**
     * Constructeur avec détails du solde.
     */
    public InsufficientBalanceException(int required, int available) {
        super(String.format("Solde insuffisant. Requis: %d, Disponible: %d", required, available));
    }
}