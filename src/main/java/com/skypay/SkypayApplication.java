package com.skypay;

import com.skypay.hotel.dto.BookingRequest;
import com.skypay.hotel.entity.Room;
import com.skypay.hotel.entity.RoomType;
import com.skypay.hotel.entity.User;
import com.skypay.hotel.service.HotelService;
import com.skypay.hotel.service.impl.HotelServiceImpl;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;

@SpringBootApplication
public class SkypayApplication {

    public static void main(String[] args) {
        // Initialize service
        HotelService hotelService = new HotelServiceImpl();

        // Create rooms using DTOs
        hotelService.setRoom(Room.builder()
                .id(1)
                .type(RoomType.STANDARD)
                .roomPricePerNight(1000)
                .build());

        hotelService.setRoom(Room.builder()
                .id(2)
                .type(RoomType.JUNIOR)
                .roomPricePerNight(2000)
                .build());

        hotelService.setRoom(Room.builder()
                .id(3)
                .type(RoomType.SUITE)
                .roomPricePerNight(3000)
                .build());

        // Create users using DTOs
        hotelService.setUser(User.builder()
                .id(1)
                .balance(5000)
                .build());

        hotelService.setUser(User.builder()
                .id(2)
                .balance(10000)
                .build());

        // Book rooms using DTOs
        try {
            hotelService.bookRoom(BookingRequest.builder()
                    .userId(1)
                    .roomNumber(1)
                    .checkIn(LocalDate.of(2026, 7, 7))
                    .checkOut(LocalDate.of(2026, 7, 8))
                    .build());


            hotelService.bookRoom(BookingRequest.builder()
                    .userId(2)
                    .roomNumber(2)
                    .checkIn(LocalDate.of(2026, 7, 7))
                    .checkOut(LocalDate.of(2026, 7, 8))
                    .build());


        } catch (Exception e) {
            System.out.println("Booking failed: " + e.getMessage());
        }

        // Print all
        hotelService.printAll();
        hotelService.printAllUsers();
    }
}
