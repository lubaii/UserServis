package com.userservice.repository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import com.userservice.entity.User;

/**
 * Интеграционные тесты для UserRepository с использованием локальной базы данных PostgreSQL.
 * Использует локальный PostgreSQL (без Docker/Testcontainers).
 */
@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager
@org.springframework.context.annotation.ComponentScan(
    basePackages = "com.userservice",
    excludeFilters = @org.springframework.context.annotation.ComponentScan.Filter(
        type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE,
        classes = com.userservice.Main.class
    )
)
@TestPropertySource(properties = {
    "spring.datasource.url=${DB_URL_TEST:jdbc:postgresql://localhost:5432/usersdb}",
    "spring.datasource.username=${DB_USERNAME_TEST:postgres}",
    "spring.datasource.password=${DB_PASSWORD_TEST:Atb31423111986}",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("UserRepository Integration Tests (Local DB)")
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Очистка базы данных перед каждым тестом для изоляции
        userRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Should save user successfully")
    void testSaveUser() {
        // Given
        User user = new User("John Doe", "john@example.com", 30);

        // When
        User saved = userRepository.save(user);

        // Then
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertTrue(saved.getId() > 0);
        assertEquals("John Doe", saved.getName());
        assertEquals("john@example.com", saved.getEmail());
        assertEquals(30, saved.getAge());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    @Order(2)
    @DisplayName("Should find user by ID")
    void testFindById() {
        // Given
        User user = new User("Jane Smith", "jane@example.com", 25);
        User saved = userRepository.save(user);

        // When
        Optional<User> found = userRepository.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals("Jane Smith", found.get().getName());
        assertEquals("jane@example.com", found.get().getEmail());
        assertEquals(25, found.get().getAge());
        assertNotNull(found.get().getCreatedAt());
    }

    @Test
    @Order(3)
    @DisplayName("Should return empty Optional when user not found")
    void testFindByIdNotFound() {
        // When
        Optional<User> found = userRepository.findById(999L);

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    @Order(4)
    @DisplayName("Should find all users")
    void testFindAll() {
        // Given
        userRepository.save(new User("User 1", "user1@example.com", 20));
        userRepository.save(new User("User 2", "user2@example.com", 30));
        userRepository.save(new User("User 3", "user3@example.com", 40));

        // When
        List<User> users = userRepository.findAll();

        // Then
        assertNotNull(users);
        assertEquals(3, users.size());
    }

    @Test
    @Order(5)
    @DisplayName("Should return empty list when no users exist")
    void testFindAllEmpty() {
        // When
        List<User> users = userRepository.findAll();

        // Then
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    @Order(6)
    @DisplayName("Should update user successfully")
    void testUpdateUser() {
        // Given
        User user = new User("Original Name", "original@example.com", 25);
        User saved = userRepository.save(user);

        // When
        saved.setName("Updated Name");
        saved.setEmail("updated@example.com");
        saved.setAge(35);
        User updated = userRepository.save(saved);

        // Then
        assertNotNull(updated);
        assertEquals(saved.getId(), updated.getId());
        assertEquals("Updated Name", updated.getName());
        assertEquals("updated@example.com", updated.getEmail());
        assertEquals(35, updated.getAge());
    }

    @Test
    @Order(7)
    @DisplayName("Should delete user successfully")
    void testDeleteUser() {
        // Given
        User user = new User("To Delete", "delete@example.com", 30);
        User saved = userRepository.save(user);

        // When
        userRepository.deleteById(saved.getId());

        // Then
        Optional<User> deleted = userRepository.findById(saved.getId());
        assertFalse(deleted.isPresent());
    }

    @Test
    @Order(8)
    @DisplayName("Should find user by email")
    void testFindByEmail() {
        // Given
        User user = new User("Test User", "test@example.com", 28);
        userRepository.save(user);

        // When
        Optional<User> found = userRepository.findByEmail("test@example.com");

        // Then
        assertTrue(found.isPresent());
        assertEquals("Test User", found.get().getName());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    @Order(9)
    @DisplayName("Should check if email exists")
    void testExistsByEmail() {
        // Given
        User user = new User("Test User", "exists@example.com", 28);
        userRepository.save(user);

        // When & Then
        assertTrue(userRepository.existsByEmail("exists@example.com"));
        assertFalse(userRepository.existsByEmail("notexists@example.com"));
    }

    @Test
    @Order(10)
    @DisplayName("Should throw exception when creating user with duplicate email")
    void testCreateUserDuplicateEmail() {
        // Given
        User user1 = new User("User 1", "duplicate@example.com", 25);
        userRepository.save(user1);

        User user2 = new User("User 2", "duplicate@example.com", 30);

        // When & Then
        assertThrows(Exception.class, () -> {
            userRepository.save(user2);
        });
    }

    @Test
    @Order(11)
    @DisplayName("Should maintain data isolation between tests")
    void testDataIsolation() {
        // Given - база должна быть пустой после setUp()
        List<User> users = userRepository.findAll();
        assertTrue(users.isEmpty(), "Database should be empty before test");

        // When
        User user = new User("Isolated User", "isolated@example.com", 28);
        User saved = userRepository.save(user);

        // Then
        Optional<User> found = userRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(1, userRepository.findAll().size());
    }
}

