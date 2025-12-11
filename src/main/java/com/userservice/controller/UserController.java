package com.userservice.controller;

import com.userservice.dto.UserCreateDTO;
import com.userservice.dto.UserDTO;
import com.userservice.dto.UserUpdateDTO;
import com.userservice.entity.User;
import com.userservice.mapper.UserMapper;
import com.userservice.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST контроллер для работы с пользователями.
 * Использует DTO для изоляции Entity от API слоя.
 */
@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    /**
     * Создание нового пользователя
     * POST /api/users
     */
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserCreateDTO createDTO) {
        User user = userMapper.toEntity(createDTO);
        User createdUser = userService.createUser(
            user.getName(),
            user.getEmail(),
            user.getAge()
        );
        UserDTO responseDTO = userMapper.toDTO(createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    /**
     * Получение пользователя по ID
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") @Min(1) Long id) {
        User user = userService.getUserById(id);
        UserDTO responseDTO = userMapper.toDTO(user);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Получение всех пользователей
     * GET /api/users
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDTO> responseDTOs = userMapper.toDTOList(users);
        return ResponseEntity.ok(responseDTOs);
    }

    /**
     * Обновление пользователя
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable("id") @Min(1) Long id,
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        User user = userService.getUserById(id);
        userMapper.updateEntityFromDTO(user, updateDTO);
        User updatedUser = userService.updateUser(
            id,
            user.getName(),
            user.getEmail(),
            user.getAge()
        );
        UserDTO responseDTO = userMapper.toDTO(updatedUser);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Удаление пользователя
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") @Min(1) Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

