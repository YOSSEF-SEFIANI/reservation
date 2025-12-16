package com.skypay.hotel.exception;

/**
 * Exception levée lorsqu'une entité (User, Room, Booking) n'est pas trouvée.
 */
public final class EntityNotFoundException extends BookingException {
    
    public EntityNotFoundException(String message) {
        super(message);
    }
    
    /**
     * Constructeur avec type d'entité et ID.
     */
    public EntityNotFoundException(String entityType, Object id) {
        super(String.format("%s avec l'ID %s introuvable", entityType, id));
    }
}