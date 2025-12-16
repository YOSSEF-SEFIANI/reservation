package com.skypay.hotel.service.impl;

import com.skypay.hotel.entity.User;
import com.skypay.hotel.service.UserService;
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
 * Implementation of UserService
 */
@Slf4j
@Getter
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final List<User> users = new CopyOnWriteArrayList<>();


    @Override
    public void setUser(int userId, int balance) {
        log.debug("setUser called - userId: {}, balance: {}", userId, balance);

        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive: " + userId);
        }
        if (balance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative: " + balance);
        }

        findUserById(userId)
                .ifPresentOrElse(
                        user -> updateUser(user, balance),
                        () -> createUser(userId, balance)
                );
    }

    @Override
    public Optional<User> findUserById(int userId) {
        return users.stream()
                .filter(u -> {
                    assert u.getId() != null;
                    return u.getId().equals(userId);
                })
                .findFirst();
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    @Override
    public void printAllUsers() {
        log.info("printAllUsers called");

        String separator = "=".repeat(80);
        System.out.println("\n" + separator);
        System.out.println("USERS (Latest to Oldest)");
        System.out.println(separator);

        users.stream()
                .sorted(Comparator.comparing(User::getCreatedDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .forEach(this::printUser);

        System.out.println(separator + "\n");
    }

    @Override
    public boolean hasSufficientBalance(int userId, int amount) {
        return findUserById(userId)
                .map(user -> user.getBalance() >= amount)
                .orElse(false);
    }

    @Override
    public void deductBalance(int userId, int amount) {
        findUserById(userId).ifPresent(user -> {
            if (user.getBalance() < amount) {
                throw new IllegalArgumentException(
                        String.format("Insufficient balance. Required: %d, Available: %d",
                                amount, user.getBalance()));
            }
            user.setBalance(user.getBalance() - amount);
            user.setLastModifiedDate(LocalDateTime.now());
            log.info("Balance deducted - User: {}, Amount: {}, New Balance: {}",
                    userId, amount, user.getBalance());
        });
    }

    // ========== Private Helper Methods ==========

    private void createUser(int userId, int balance) {
        User user = User.builder()
                .id(userId)
                .balance(balance)
                .createdDate(LocalDateTime.now())
                .build();
        users.add(user);
        log.info("User created - ID: {}, Balance: {}", userId, balance);
    }

    private void updateUser(User user, int balance) {
        user.setBalance(balance);
        user.setLastModifiedDate(LocalDateTime.now());
        log.info("User updated - ID: {}, Balance: {}", user.getId(), balance);
    }

    private void printUser(User user) {
        System.out.printf("User %-5d | Balance: %-8d | Created: %s%n",
                user.getId(),
                user.getBalance(),
                user.getCreatedDate());
    }

}