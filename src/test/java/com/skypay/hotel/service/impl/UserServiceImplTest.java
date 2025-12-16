package com.skypay.hotel.service.impl;

import com.skypay.hotel.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("UserService Tests")
class UserServiceImplTest {

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl();
    }

    @Test
    @DisplayName("Should create new user when user does not exist")
    void shouldCreateNewUserWhenUserDoesNotExist() {
        // When
        userService.setUser(1, 5000);

        // Then
        Optional<User> user = userService.findUserById(1);
        assertThat(user).isPresent();
        assertThat(user.get().getId()).isEqualTo(1);
        assertThat(user.get().getBalance()).isEqualTo(5000);
        assertThat(user.get().getCreatedDate()).isNotNull();
    }

    @Test
    @DisplayName("Should update existing user when user already exists")
    void shouldUpdateExistingUserWhenUserAlreadyExists() {
        // Given
        userService.setUser(1, 5000);

        // When
        userService.setUser(1, 10000);

        // Then
        Optional<User> user = userService.findUserById(1);
        assertThat(user).isPresent();
        assertThat(user.get().getBalance()).isEqualTo(10000);
        assertThat(user.get().getLastModifiedDate()).isNotNull();
    }

    @Test
    @DisplayName("Should return empty Optional when user not found")
    void shouldReturnEmptyOptionalWhenUserNotFound() {
        // When
        Optional<User> user = userService.findUserById(999);

        // Then
        assertThat(user).isEmpty();
    }

    @Test
    @DisplayName("Should return all users")
    void shouldReturnAllUsers() {
        // Given
        userService.setUser(1, 5000);
        userService.setUser(2, 10000);
        userService.setUser(3, 15000);

        // When
        List<User> users = userService.getAllUsers();

        // Then
        assertThat(users).hasSize(3);
        assertThat(users).extracting(User::getId)
                .containsExactlyInAnyOrder(1, 2, 3);
    }

    @Test
    @DisplayName("Should return true when user has sufficient balance")
    void shouldReturnTrueWhenUserHasSufficientBalance() {
        // Given
        userService.setUser(1, 5000);

        // When
        boolean hasSufficient = userService.hasSufficientBalance(1, 3000);

        // Then
        assertThat(hasSufficient).isTrue();
    }

    @Test
    @DisplayName("Should return true when user has exact balance")
    void shouldReturnTrueWhenUserHasExactBalance() {
        // Given
        userService.setUser(1, 5000);

        // When
        boolean hasSufficient = userService.hasSufficientBalance(1, 5000);

        // Then
        assertThat(hasSufficient).isTrue();
    }

    @Test
    @DisplayName("Should return false when user has insufficient balance")
    void shouldReturnFalseWhenUserHasInsufficientBalance() {
        // Given
        userService.setUser(1, 5000);

        // When
        boolean hasSufficient = userService.hasSufficientBalance(1, 6000);

        // Then
        assertThat(hasSufficient).isFalse();
    }

    @Test
    @DisplayName("Should return false when user does not exist")
    void shouldReturnFalseWhenUserDoesNotExist() {
        // When
        boolean hasSufficient = userService.hasSufficientBalance(999, 1000);

        // Then
        assertThat(hasSufficient).isFalse();
    }

    @Test
    @DisplayName("Should deduct balance from user")
    void shouldDeductBalanceFromUser() {
        // Given
        userService.setUser(1, 5000);

        // When
        userService.deductBalance(1, 2000);

        // Then
        Optional<User> user = userService.findUserById(1);
        assertThat(user).isPresent();
        assertThat(user.get().getBalance()).isEqualTo(3000);
        assertThat(user.get().getLastModifiedDate()).isNotNull();
    }

    @Test
    @DisplayName("Should deduct balance to zero")
    void shouldDeductBalanceToZero() {
        // Given
        userService.setUser(1, 5000);

        // When
        userService.deductBalance(1, 5000);

        // Then
        Optional<User> user = userService.findUserById(1);
        assertThat(user).isPresent();
        assertThat(user.get().getBalance()).isZero();
    }

    @Test
    @DisplayName("Should handle multiple balance deductions")
    void shouldHandleMultipleBalanceDeductions() {
        // Given
        userService.setUser(1, 10000);

        // When
        userService.deductBalance(1, 2000);
        userService.deductBalance(1, 3000);
        userService.deductBalance(1, 1000);

        // Then
        Optional<User> user = userService.findUserById(1);
        assertThat(user).isPresent();
        assertThat(user.get().getBalance()).isEqualTo(4000);
    }

    @Test
    @DisplayName("Should print all users without errors")
    void shouldPrintAllUsersWithoutErrors() {
        // Given
        userService.setUser(1, 5000);
        userService.setUser(2, 10000);

        // When & Then - should not throw exception
        userService.printAllUsers();
    }

    @Test
    @DisplayName("Should do nothing when deducting from non-existent user")
    void shouldDoNothingWhenDeductingFromNonExistentUser() {
        // When
        userService.deductBalance(999, 1000);

        // Then
        Optional<User> user = userService.findUserById(999);
        assertThat(user).isEmpty();
    }

    @Test
    @DisplayName("Should throw exception when trying to deduct more than balance")
    void shouldThrowExceptionWhenDeductingMoreThanBalance() {
        // Given
        userService.setUser(1, 1000);

        // When & Then
        assertThatThrownBy(() -> userService.deductBalance(1, 2000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Insufficient balance")
                .hasMessageContaining("Required: 2000")
                .hasMessageContaining("Available: 1000");
    }

    @Test
    @DisplayName("Should throw exception when user balance is negative")
    void shouldThrowExceptionWhenUserBalanceIsNegative() {
        // When & Then
        assertThatThrownBy(() -> userService.setUser(1, -1000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Balance cannot be negative");
    }

    @Test
    @DisplayName("Should throw exception when user ID is zero")
    void shouldThrowExceptionWhenUserIdIsZero() {
        // When & Then
        assertThatThrownBy(() -> userService.setUser(0, 1000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User ID must be positive");
    }

    @Test
    @DisplayName("Should throw exception when user ID is negative")
    void shouldThrowExceptionWhenUserIdIsNegative() {
        // When & Then
        assertThatThrownBy(() -> userService.setUser(-1, 1000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User ID must be positive");
    }
}
