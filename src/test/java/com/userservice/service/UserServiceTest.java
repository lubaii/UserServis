package com.userservice.service;

import com.userservice.dao.UserDAO;
import com.userservice.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("John Doe", "john@example.com", 30);
        testUser.setId(1L);
        testUser.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create user successfully with valid data")
    void testCreateUserSuccess() {
        // Given
        when(userDAO.create(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return 1L;
        });

        // When
        User created = userService.createUser("John Doe", "john@example.com", 30);

        // Then
        assertNotNull(created);
        assertEquals(1L, created.getId());
        assertEquals("John Doe", created.getName());
        assertEquals("john@example.com", created.getEmail());
        assertEquals(30, created.getAge());
        verify(userDAO, times(1)).create(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when creating user with empty name")
    void testCreateUserEmptyName() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser("", "john@example.com", 30);
        });

        assertEquals("Name cannot be empty", exception.getMessage());
        verify(userDAO, never()).create(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when creating user with null name")
    void testCreateUserNullName() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(null, "john@example.com", 30);
        });

        assertEquals("Name cannot be empty", exception.getMessage());
        verify(userDAO, never()).create(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when creating user with invalid email")
    void testCreateUserInvalidEmail() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser("John Doe", "invalid-email", 30);
        });

        assertEquals("Invalid email format", exception.getMessage());
        verify(userDAO, never()).create(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when creating user with null email")
    void testCreateUserNullEmail() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser("John Doe", null, 30);
        });

        assertEquals("Email cannot be empty", exception.getMessage());
        verify(userDAO, never()).create(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when creating user with negative age")
    void testCreateUserNegativeAge() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser("John Doe", "john@example.com", -1);
        });

        assertEquals("Age must be between 0 and 150", exception.getMessage());
        verify(userDAO, never()).create(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when creating user with age over 150")
    void testCreateUserAgeOverLimit() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser("John Doe", "john@example.com", 151);
        });

        assertEquals("Age must be between 0 and 150", exception.getMessage());
        verify(userDAO, never()).create(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when creating user with null age")
    void testCreateUserNullAge() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser("John Doe", "john@example.com", null);
        });

        assertEquals("Age cannot be null", exception.getMessage());
        verify(userDAO, never()).create(any(User.class));
    }

    @Test
    @DisplayName("Should get user by ID successfully")
    void testGetUserByIdSuccess() {
        // Given
        when(userDAO.read(1L)).thenReturn(testUser);

        // When
        User found = userService.getUserById(1L);

        // Then
        assertNotNull(found);
        assertEquals(1L, found.getId());
        assertEquals("John Doe", found.getName());
        verify(userDAO, times(1)).read(1L);
    }

    @Test
    @DisplayName("Should throw exception when getting user with null ID")
    void testGetUserByIdNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.getUserById(null);
        });

        assertEquals("User ID must be positive", exception.getMessage());
        verify(userDAO, never()).read(anyLong());
    }

    @Test
    @DisplayName("Should throw exception when getting user with negative ID")
    void testGetUserByIdNegative() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.getUserById(-1L);
        });

        assertEquals("User ID must be positive", exception.getMessage());
        verify(userDAO, never()).read(anyLong());
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void testGetUserByIdNotFound() {
        // Given
        when(userDAO.read(999L)).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.getUserById(999L);
        });

        assertEquals("User with ID 999 not found", exception.getMessage());
        verify(userDAO, times(1)).read(999L);
    }

    @Test
    @DisplayName("Should get all users successfully")
    void testGetAllUsersSuccess() {
        // Given
        User user1 = new User("User 1", "user1@example.com", 25);
        user1.setId(1L);
        User user2 = new User("User 2", "user2@example.com", 30);
        user2.setId(2L);
        List<User> users = Arrays.asList(user1, user2);
        
        when(userDAO.readAll()).thenReturn(users);

        // When
        List<User> result = userService.getAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userDAO, times(1)).readAll();
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void testGetAllUsersEmpty() {
        // Given
        when(userDAO.readAll()).thenReturn(Collections.emptyList());

        // When
        List<User> result = userService.getAllUsers();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userDAO, times(1)).readAll();
    }

    @Test
    @DisplayName("Should update user successfully with all fields")
    void testUpdateUserSuccess() {
        // Given
        when(userDAO.read(1L)).thenReturn(testUser);
        doNothing().when(userDAO).update(any(User.class));

        // When
        User updated = userService.updateUser(1L, "Jane Smith", "jane@example.com", 35);

        // Then
        assertNotNull(updated);
        assertEquals("Jane Smith", updated.getName());
        assertEquals("jane@example.com", updated.getEmail());
        assertEquals(35, updated.getAge());
        verify(userDAO, times(1)).read(1L);
        verify(userDAO, times(1)).update(any(User.class));
    }

    @Test
    @DisplayName("Should update user with partial data")
    void testUpdateUserPartial() {
        // Given
        when(userDAO.read(1L)).thenReturn(testUser);
        doNothing().when(userDAO).update(any(User.class));

        // When - обновляем только имя
        User updated = userService.updateUser(1L, "Jane Smith", null, null);

        // Then
        assertNotNull(updated);
        assertEquals("Jane Smith", updated.getName());
        assertEquals("john@example.com", updated.getEmail()); // осталось прежним
        assertEquals(30, updated.getAge()); // осталось прежним
        verify(userDAO, times(1)).read(1L);
        verify(userDAO, times(1)).update(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent user")
    void testUpdateUserNotFound() {
        // Given
        when(userDAO.read(999L)).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(999L, "New Name", "new@example.com", 40);
        });

        assertEquals("User with ID 999 not found", exception.getMessage());
        verify(userDAO, times(1)).read(999L);
        verify(userDAO, never()).update(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when updating with invalid email")
    void testUpdateUserInvalidEmail() {
        // Given
        when(userDAO.read(1L)).thenReturn(testUser);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(1L, null, "invalid-email", null);
        });

        assertEquals("Invalid email format", exception.getMessage());
        verify(userDAO, times(1)).read(1L);
        verify(userDAO, never()).update(any(User.class));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void testDeleteUserSuccess() {
        // Given
        doNothing().when(userDAO).delete(1L);

        // When
        userService.deleteUser(1L);

        // Then
        verify(userDAO, times(1)).delete(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting user with null ID")
    void testDeleteUserNullId() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteUser(null);
        });

        assertEquals("User ID must be positive", exception.getMessage());
        verify(userDAO, never()).delete(anyLong());
    }

    @Test
    @DisplayName("Should throw exception when deleting user with negative ID")
    void testDeleteUserNegativeId() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteUser(-1L);
        });

        assertEquals("User ID must be positive", exception.getMessage());
        verify(userDAO, never()).delete(anyLong());
    }

    @Test
    @DisplayName("Should validate name length")
    void testCreateUserNameTooLong() {
        // Given
        String longName = "a".repeat(101);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(longName, "john@example.com", 30);
        });

        assertEquals("Name cannot exceed 100 characters", exception.getMessage());
        verify(userDAO, never()).create(any(User.class));
    }

    @Test
    @DisplayName("Should validate email length")
    void testCreateUserEmailTooLong() {
        // Given
        String longEmail = "a".repeat(95) + "@example.com";

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser("John Doe", longEmail, 30);
        });

        assertEquals("Email cannot exceed 100 characters", exception.getMessage());
        verify(userDAO, never()).create(any(User.class));
    }
}

