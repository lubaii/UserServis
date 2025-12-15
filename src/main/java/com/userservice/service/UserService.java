package com.userservice.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.userservice.entity.User;
import com.userservice.repository.UserRepository;

@Service
@Transactional
public class UserService {
    private static final Logger logger = LogManager.getLogger(UserService.class);
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String name, String email, Integer age) {
        logger.debug("Creating user with name: {}, email: {}, age: {}", name, email, age);
        
        validateUserData(name, email, age);
        
        // Проверка на дубликат email
        if (userRepository.existsByEmail(email)) {
            logger.warn("User with email {} already exists", email);
            throw new IllegalArgumentException("User with this email already exists");
        }
        
        User user = new User(name, email, age);
        user = userRepository.save(user);
        
        logger.info("User created successfully with ID: {}", user.getId());
        return user;
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        logger.debug("Getting user by ID: {}", id);
        
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User with ID {} not found", id);
                    return new IllegalArgumentException("User with ID " + id + " not found");
                });
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        logger.debug("Getting all users");
        return userRepository.findAll();
    }

    public User updateUser(Long id, String name, String email, Integer age) {
        logger.debug("Updating user with ID: {}", id);
        
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + id + " not found"));
        
        if (name != null && !name.trim().isEmpty()) {
            validateName(name);
            user.setName(name.trim());
        }
        
        if (email != null && !email.trim().isEmpty()) {
            validateEmail(email);
            // Проверка на дубликат email (кроме текущего пользователя)
            if (!email.equals(user.getEmail()) && userRepository.existsByEmail(email)) {
                throw new IllegalArgumentException("User with this email already exists");
            }
            user.setEmail(email.trim());
        }
        
        if (age != null) {
            validateAge(age);
            user.setAge(age);
        }
        
        user = userRepository.save(user);
        logger.info("User with ID {} updated successfully", id);
        return user;
    }

    public void deleteUser(Long id) {
        logger.debug("Deleting user with ID: {}", id);
        
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User with ID " + id + " not found");
        }
        
        userRepository.deleteById(id);
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

