package com.skypay.hotel.service.impl;

import com.skypay.hotel.entity.Room;
import com.skypay.hotel.entity.RoomType;
import com.skypay.hotel.service.RoomService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementation of RoomService
 */
@Slf4j
@Getter
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final List<Room> rooms = new CopyOnWriteArrayList<>();


    @Override
    public void setRoom(int roomNumber, RoomType roomType, int roomPricePerNight) {
        if (roomNumber <= 0) {
            throw new IllegalArgumentException("Room number must be positive: " + roomNumber);
        }
        if (roomPricePerNight < 0) {
            throw new IllegalArgumentException("Room price cannot be negative: " + roomPricePerNight);
        }

        findRoomByNumber(roomNumber)
                .ifPresentOrElse(
                        room -> updateRoom(room, roomType, roomPricePerNight),
                        () -> createRoom(roomNumber, roomType, roomPricePerNight)
                );
    }

    @Override
    public Optional<Room> findRoomByNumber(int roomNumber) {
        return rooms.stream()
                .filter(r -> r.getRoomNumber().equals(roomNumber))
                .findFirst();
    }

    @Override
    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms);
    }

    @Override
    public void printAllRooms() {
        log.info("printAllRooms called");

        String separator = "=".repeat(80);
        System.out.println("\n" + separator);
        System.out.println("ROOMS (Latest to Oldest)");
        System.out.println(separator);

        rooms.stream()
                .sorted(Comparator.comparing(Room::getCreatedDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .forEach(this::printRoom);

        System.out.println(separator + "\n");
    }

    // ========== Private Helper Methods ==========

    private void createRoom(int roomNumber, RoomType roomType, int roomPricePerNight) {
        Room room = Room.builder()
                .id(roomNumber)
                .type(roomType)
                .roomPricePerNight(roomPricePerNight)
                .createdDate(LocalDateTime.now())
                .build();
        rooms.add(room);
        log.info("Room created - Number: {}, Type: {}, Price: {}",
                roomNumber, roomType, roomPricePerNight);
    }

    private void updateRoom(Room room, RoomType roomType, int roomPricePerNight) {
        room.setType(roomType);
        room.setRoomPricePerNight(roomPricePerNight);
        room.setLastModifiedDate(LocalDateTime.now());
        log.info("Room updated - Number: {}, Type: {}, Price: {}",
                room.getRoomNumber(), roomType, roomPricePerNight);
    }

    private void printRoom(Room room) {
        System.out.printf("Room %-5d | Type: %-10s | Price/night: %-6d | Created: %s%n",
                room.getRoomNumber(),
                room.getType(),
                room.getRoomPricePerNight(),
                room.getCreatedDate());
    }

}