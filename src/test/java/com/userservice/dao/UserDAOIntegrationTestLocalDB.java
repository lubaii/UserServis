package com.userservice.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.userservice.entity.User;
import com.userservice.util.TestHibernateUtil;

/**
 * Интеграционные тесты для UserDAO с использованием локальной базы данных PostgreSQL.
 * Использует локальный PostgreSQL из hibernate.cfg.xml (без Docker/Testcontainers).
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("UserDAO Integration Tests (Local DB)")
class UserDAOIntegrationTestLocalDB {

    private static UserDAO userDAO;
    private static SessionFactory sessionFactory;

    @BeforeAll
    static void setUp() {
        // Читаем настройки из переменных окружения или используем значения по умолчанию
        String jdbcUrl = System.getenv("DB_URL");
        if (jdbcUrl == null || jdbcUrl.isEmpty()) {
            jdbcUrl = System.getProperty("db.url", "jdbc:postgresql://localhost:5432/usersdb");
        }
        
        String username = System.getenv("DB_USERNAME");
        if (username == null || username.isEmpty()) {
            username = System.getProperty("db.username", "postgres");
        }
        
        String password = System.getenv("DB_PASSWORD");
        if (password == null || password.isEmpty()) {
            password = System.getProperty("db.password");
            if (password == null || password.isEmpty()) {
                throw new IllegalStateException(
                    "Database password not found! " +
                    "Please set DB_PASSWORD environment variable or db.password system property. " +
                    "Example: export DB_PASSWORD=your_password"
                );
            }
        }
        
        sessionFactory = TestHibernateUtil.createSessionFactory(jdbcUrl, username, password);
        userDAO = new UserDAO(sessionFactory);
    }

    @AfterAll
    static void tearDown() {
        TestHibernateUtil.shutdown();
    }

    @BeforeEach
    void clearDatabase() {
        // Очистка базы данных перед каждым тестом для изоляции
        try {
            List<User> users = userDAO.readAll();
            for (User user : users) {
                try {
                    userDAO.delete(user.getId());
                } catch (Exception e) {
                    // Игнорируем ошибки при удалении
                }
            }
        } catch (Exception e) {
            // Игнорируем ошибки при очистке
        }
    }

    @Test
    @Order(1)
    @DisplayName("Should create user successfully")
    void testCreateUser() {
        // Given
        User user = new User("John Doe", "john@example.com", 30);

        // When
        Long id = userDAO.create(user);

        // Then
        assertNotNull(id);
        assertTrue(id > 0);
        assertEquals(user.getId(), id);
    }

    @Test
    @Order(2)
    @DisplayName("Should read user by ID")
    void testReadUser() {
        // Given
        User user = new User("Jane Smith", "jane@example.com", 25);
        Long id = userDAO.create(user);

        // When
        User found = userDAO.read(id);

        // Then
        assertNotNull(found);
        assertEquals(id, found.getId());
        assertEquals("Jane Smith", found.getName());
        assertEquals("jane@example.com", found.getEmail());
        assertEquals(25, found.getAge());
        assertNotNull(found.getCreatedAt());
    }

    @Test
    @Order(3)
    @DisplayName("Should return null when user not found")
    void testReadUserNotFound() {
        // When
        User found = userDAO.read(999L);

        // Then
        assertNull(found);
    }

    @Test
    @Order(4)
    @DisplayName("Should read all users")
    void testReadAllUsers() {
        // Given
        userDAO.create(new User("User 1", "user1@example.com", 20));
        userDAO.create(new User("User 2", "user2@example.com", 30));
        userDAO.create(new User("User 3", "user3@example.com", 40));

        // When
        List<User> users = userDAO.readAll();

        // Then
        assertNotNull(users);
        assertEquals(3, users.size());
    }

    @Test
    @Order(5)
    @DisplayName("Should return empty list when no users exist")
    void testReadAllUsersEmpty() {
        // When
        List<User> users = userDAO.readAll();

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
        Long id = userDAO.create(user);
        user.setId(id);

        // When
        user.setName("Updated Name");
        user.setEmail("updated@example.com");
        user.setAge(35);
        userDAO.update(user);

        // Then
        User updated = userDAO.read(id);
        assertNotNull(updated);
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
        Long id = userDAO.create(user);

        // When
        userDAO.delete(id);

        // Then
        User deleted = userDAO.read(id);
        assertNull(deleted);
    }

    @Test
    @Order(8)
    @DisplayName("Should throw exception when deleting non-existent user")
    void testDeleteUserNotFound() {
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userDAO.delete(999L);
        });
        
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    @Order(9)
    @DisplayName("Should throw exception when creating user with duplicate email")
    void testCreateUserDuplicateEmail() {
        // Given
        User user1 = new User("User 1", "duplicate@example.com", 25);
        userDAO.create(user1);

        User user2 = new User("User 2", "duplicate@example.com", 30);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userDAO.create(user2);
        });
        
        assertTrue(exception.getMessage().contains("email already exists"));
    }

    @Test
    @Order(10)
    @DisplayName("Should throw exception when updating user with duplicate email")
    void testUpdateUserDuplicateEmail() {
        // Given
        User user1 = new User("User 1", "email1@example.com", 25);
        Long id1 = userDAO.create(user1);
        user1.setId(id1);

        User user2 = new User("User 2", "email2@example.com", 30);
        Long id2 = userDAO.create(user2);
        user2.setId(id2);

        // When - пытаемся изменить email user2 на email user1
        user2.setEmail("email1@example.com");

        // Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userDAO.update(user2);
        });
        
        // Проверяем, что исключение содержит информацию о дубликате email
        // Может быть либо "email already exists", либо общее сообщение об ошибке
        assertTrue(exception.getMessage().contains("email already exists") || 
                   exception.getMessage().contains("Failed to update user"),
                   "Exception message should indicate email conflict or update failure");
    }

    @Test
    @Order(11)
    @DisplayName("Should maintain data isolation between tests")
    void testDataIsolation() {
        // Given - база должна быть пустой после clearDatabase()
        List<User> users = userDAO.readAll();
        assertTrue(users.isEmpty(), "Database should be empty before test");

        // When
        User user = new User("Isolated User", "isolated@example.com", 28);
        Long id = userDAO.create(user);

        // Then
        User found = userDAO.read(id);
        assertNotNull(found);
        assertEquals(1, userDAO.readAll().size());
    }
}

