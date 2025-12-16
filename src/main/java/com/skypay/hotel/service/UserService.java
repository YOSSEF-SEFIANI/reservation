package com.skypay.hotel.service;


import com.skypay.hotel.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for User management
 */
public interface UserService {

    /**
     * Creates a user if it does not already exist.
     *
     * @param userId  the user ID
     * @param balance the user's balance
     */
    void setUser(int userId, int balance);

    /**
     * Finds a user by ID
     *
     * @param userId the user ID
     * @return Optional containing the user if found
     */
    Optional<User> findUserById(int userId);

    /**
     * Gets all users
     *
     * @return list of all users
     */
    List<User> getAllUsers();

    /**
     * Prints all users from latest to oldest created
     */
    void printAllUsers();

    /**
     * Checks if user has sufficient balance
     *
     * @param userId the user ID
     * @param amount the amount to check
     * @return true if user has sufficient balance
     */
    boolean hasSufficientBalance(int userId, int amount);

    /**
     * Deducts balance from user account
     *
     * @param userId the user ID
     * @param amount the amount to deduct
     */
    void deductBalance(int userId, int amount);

}