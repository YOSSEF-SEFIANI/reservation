package com.skypay.hotel.service.impl;

import com.skypay.hotel.entity.Room;
import com.skypay.hotel.entity.RoomType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("RoomService Tests")
class RoomServiceImplTest {

    private RoomServiceImpl roomService;

    @BeforeEach
    void setUp() {
        roomService = new RoomServiceImpl();
    }

    @Test
    @DisplayName("Should create new room when room does not exist")
    void shouldCreateNewRoomWhenRoomDoesNotExist() {
        // When
        roomService.setRoom(1, RoomType.STANDARD, 1000);

        // Then
        Optional<Room> room = roomService.findRoomByNumber(1);
        assertThat(room).isPresent();
        assertThat(room.get().getRoomNumber()).isEqualTo(1);
        assertThat(room.get().getType()).isEqualTo(RoomType.STANDARD);
        assertThat(room.get().getRoomPricePerNight()).isEqualTo(1000);
        assertThat(room.get().getCreatedDate()).isNotNull();
    }

    @Test
    @DisplayName("Should update existing room when room already exists")
    void shouldUpdateExistingRoomWhenRoomAlreadyExists() {
        // Given
        roomService.setRoom(1, RoomType.STANDARD, 1000);

        // When
        roomService.setRoom(1, RoomType.JUNIOR, 2000);

        // Then
        Optional<Room> room = roomService.findRoomByNumber(1);
        assertThat(room).isPresent();
        assertThat(room.get().getType()).isEqualTo(RoomType.JUNIOR);
        assertThat(room.get().getRoomPricePerNight()).isEqualTo(2000);
        assertThat(room.get().getLastModifiedDate()).isNotNull();
    }

    @Test
    @DisplayName("Should return empty Optional when room not found")
    void shouldReturnEmptyOptionalWhenRoomNotFound() {
        // When
        Optional<Room> room = roomService.findRoomByNumber(999);

        // Then
        assertThat(room).isEmpty();
    }

    @Test
    @DisplayName("Should return all rooms")
    void shouldReturnAllRooms() {
        // Given
        roomService.setRoom(1, RoomType.STANDARD, 1000);
        roomService.setRoom(2, RoomType.JUNIOR, 2000);
        roomService.setRoom(3, RoomType.SUITE, 3000);

        // When
        List<Room> rooms = roomService.getAllRooms();

        // Then
        assertThat(rooms).hasSize(3);
        assertThat(rooms).extracting(Room::getRoomNumber)
                .containsExactlyInAnyOrder(1, 2, 3);
    }

    @Test
    @DisplayName("Should maintain separate room instances")
    void shouldMaintainSeparateRoomInstances() {
        // Given
        roomService.setRoom(1, RoomType.STANDARD, 1000);
        roomService.setRoom(2, RoomType.JUNIOR, 2000);

        // When
        Optional<Room> room1 = roomService.findRoomByNumber(1);
        Optional<Room> room2 = roomService.findRoomByNumber(2);

        // Then
        assertThat(room1).isPresent();
        assertThat(room2).isPresent();
        assertThat(room1.get()).isNotSameAs(room2.get());
        assertThat(room1.get().getRoomNumber()).isNotEqualTo(room2.get().getRoomNumber());
    }

    @Test
    @DisplayName("Should create rooms with all room types")
    void shouldCreateRoomsWithAllRoomTypes() {
        // When
        roomService.setRoom(1, RoomType.STANDARD, 1000);
        roomService.setRoom(2, RoomType.JUNIOR, 2000);
        roomService.setRoom(3, RoomType.SUITE, 3000);

        // Then
        assertThat(roomService.findRoomByNumber(1).get().getType()).isEqualTo(RoomType.STANDARD);
        assertThat(roomService.findRoomByNumber(2).get().getType()).isEqualTo(RoomType.JUNIOR);
        assertThat(roomService.findRoomByNumber(3).get().getType()).isEqualTo(RoomType.SUITE);
    }

    @Test
    @DisplayName("Should print all rooms without errors")
    void shouldPrintAllRoomsWithoutErrors() {
        // Given
        roomService.setRoom(1, RoomType.STANDARD, 1000);
        roomService.setRoom(2, RoomType.JUNIOR, 2000);

        // When & Then - should not throw exception
        roomService.printAllRooms();
    }

    @Test
    @DisplayName("Should throw exception when room price is negative")
    void shouldThrowExceptionWhenRoomPriceIsNegative() {
        // When & Then
        assertThatThrownBy(() -> roomService.setRoom(1, RoomType.STANDARD, -1000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Room price cannot be negative");
    }

    @Test
    @DisplayName("Should throw exception when room number is zero")
    void shouldThrowExceptionWhenRoomNumberIsZero() {
        // When & Then
        assertThatThrownBy(() -> roomService.setRoom(0, RoomType.STANDARD, 1000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Room number must be positive");
    }

    @Test
    @DisplayName("Should throw exception when room number is negative")
    void shouldThrowExceptionWhenRoomNumberIsNegative() {
        // When & Then
        assertThatThrownBy(() -> roomService.setRoom(-1, RoomType.STANDARD, 1000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Room number must be positive");
    }
}
