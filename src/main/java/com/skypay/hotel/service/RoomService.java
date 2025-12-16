package com.skypay.hotel.service;

import com.skypay.hotel.entity.Room;
import com.skypay.hotel.entity.RoomType;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Room management
 */
public interface RoomService {

    /**
     * Creates a room if it does not already exist.
     * The function setRoom(... ) should not impact the previously created bookings.
     *
     * @param roomNumber the room number
     * @param roomType the type of room (standard, junior, suite)
     * @param roomPricePerNight the price per night for booking
     */
    void setRoom(int roomNumber, RoomType roomType, int roomPricePerNight);

    /**
     * Finds a room by its room number
     *
     * @param roomNumber the room number
     * @return Optional containing the room if found
     */
    Optional<Room> findRoomByNumber(int roomNumber);

    /**
     * Gets all rooms
     *
     * @return list of all rooms
     */
    List<Room> getAllRooms();

    /**
     * Prints all rooms from latest to oldest created
     */
    void printAllRooms();

}