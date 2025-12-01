package com.userservice.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.userservice.dao.UserDAO;
import com.userservice.entity.User;

public class UserService {
    private static final Logger logger = LogManager.getLogger(UserService.class);
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User createUser(String name, String email, Integer age) {
        logger.debug("Creating user with name: {}, email: {}, age: {}", name, email, age);
        
        validateUserData(name, email, age);
        
        User user = new User(name, email, age);
        Long id = userDAO.create(user);
        user.setId(id);
        
        logger.info("User created successfully with ID: {}", id);
        return user;
    }

    public User getUserById(Long id) {
        logger.debug("Getting user by ID: {}", id);
        
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        
        User user = userDAO.read(id);
        if (user == null) {
            logger.warn("User with ID {} not found", id);
            throw new IllegalArgumentException("User with ID " + id + " not found");
        }
        
        return user;
    }

    public List<User> getAllUsers() {
        logger.debug("Getting all users");
        return userDAO.readAll();
    }

    public User updateUser(Long id, String name, String email, Integer age) {
        logger.debug("Updating user with ID: {}", id);
        
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        
        User user = userDAO.read(id);
        if (user == null) {
            throw new IllegalArgumentException("User with ID " + id + " not found");
        }
        
        if (name != null && !name.trim().isEmpty()) {
            validateName(name);
            user.setName(name.trim());
        }
        
        if (email != null && !email.trim().isEmpty()) {
            validateEmail(email);
            user.setEmail(email.trim());
        }
        
        if (age != null) {
            validateAge(age);
            user.setAge(age);
        }
        
        userDAO.update(user);
        logger.info("User with ID {} updated successfully", id);
        return user;
    }

    public void deleteUser(Long id) {
        logger.debug("Deleting user with ID: {}", id);
        
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        
        userDAO.delete(id);
        logger.info("User with ID {} deleted successfully", id);
    }

    private void validateUserData(String name, String email, Integer age) {
        validateName(name);
        validateEmail(email);
        validateAge(age);
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Name cannot exceed 100 characters");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (email.length() > 100) {
            throw new IllegalArgumentException("Email cannot exceed 100 characters");
        }
    }

    private void validateAge(Integer age) {
        if (age == null) {
            throw new IllegalArgumentException("Age cannot be null");
        }
        if (age < 0 || age > 150) {
            throw new IllegalArgumentException("Age must be between 0 and 150");
        }
    }
}

