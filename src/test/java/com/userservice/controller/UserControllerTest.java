package com.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.userservice.dto.UserCreateDTO;
import com.userservice.dto.UserDTO;
import com.userservice.dto.UserUpdateDTO;
import com.userservice.entity.User;
import com.userservice.mapper.UserMapper;
import com.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("UserController API Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    private User testUser;
    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        testUser = new User("John Doe", "john@example.com", 30);
        testUser.setId(1L);
        testUser.setCreatedAt(LocalDateTime.now());

        testUserDTO = new UserDTO(
            1L,
            "John Doe",
            "john@example.com",
            30,
            testUser.getCreatedAt()
        );
    }

    // ========== CREATE USER TESTS ==========

    @Test
    @DisplayName("POST /api/users - Should create user successfully")
    void testCreateUser_Success() throws Exception {
        // Given
        UserCreateDTO createDTO = new UserCreateDTO("John Doe", "john@example.com", 30);
        User createdUser = new User("John Doe", "john@example.com", 30);
        createdUser.setId(1L);
        createdUser.setCreatedAt(LocalDateTime.now());
        UserDTO responseDTO = new UserDTO(1L, "John Doe", "john@example.com", 30, createdUser.getCreatedAt());

        when(userMapper.toEntity(any(UserCreateDTO.class))).thenReturn(testUser);
        when(userService.createUser(anyString(), anyString(), anyInt())).thenReturn(createdUser);
        when(userMapper.toDTO(any(User.class))).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.age").value(30))
                .andExpect(jsonPath("$.createdAt").exists());

        verify(userMapper, times(1)).toEntity(any(UserCreateDTO.class));
        verify(userService, times(1)).createUser("John Doe", "john@example.com", 30);
        verify(userMapper, times(1)).toDTO(any(User.class));
    }

    @Test
    @DisplayName("POST /api/users - Should return 400 when name is blank")
    void testCreateUser_BlankName() throws Exception {
        // Given
        UserCreateDTO createDTO = new UserCreateDTO("", "john@example.com", 30);

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(anyString(), anyString(), anyInt());
    }

    @Test
    @DisplayName("POST /api/users - Should return 400 when email is invalid")
    void testCreateUser_InvalidEmail() throws Exception {
        // Given
        UserCreateDTO createDTO = new UserCreateDTO("John Doe", "invalid-email", 30);

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(anyString(), anyString(), anyInt());
    }

    @Test
    @DisplayName("POST /api/users - Should return 400 when age is negative")
    void testCreateUser_NegativeAge() throws Exception {
        // Given
        UserCreateDTO createDTO = new UserCreateDTO("John Doe", "john@example.com", -1);

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(anyString(), anyString(), anyInt());
    }

    @Test
    @DisplayName("POST /api/users - Should return 400 when age exceeds 150")
    void testCreateUser_AgeExceedsLimit() throws Exception {
        // Given
        UserCreateDTO createDTO = new UserCreateDTO("John Doe", "john@example.com", 151);

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(anyString(), anyString(), anyInt());
    }

    @Test
    @DisplayName("POST /api/users - Should return 400 when service throws IllegalArgumentException")
    void testCreateUser_ServiceThrowsException() throws Exception {
        // Given
        UserCreateDTO createDTO = new UserCreateDTO("John Doe", "john@example.com", 30);
        when(userMapper.toEntity(any(UserCreateDTO.class))).thenReturn(testUser);
        when(userService.createUser(anyString(), anyString(), anyInt()))
                .thenThrow(new IllegalArgumentException("User with this email already exists"));

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("User with this email already exists"));
    }

    // ========== GET USER BY ID TESTS ==========

    @Test
    @DisplayName("GET /api/users/{id} - Should return user successfully")
    void testGetUserById_Success() throws Exception {
        // Given
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(userMapper.toDTO(any(User.class))).thenReturn(testUserDTO);

        // When & Then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.age").value(30));

        verify(userService, times(1)).getUserById(1L);
        verify(userMapper, times(1)).toDTO(any(User.class));
    }

    @Test
    @DisplayName("GET /api/users/{id} - Should return 400 when id is invalid")
    void testGetUserById_InvalidId() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users/0"))
                .andExpect(status().isBadRequest());

        verify(userService, never()).getUserById(anyLong());
    }

    @Test
    @DisplayName("GET /api/users/{id} - Should return 400 when user not found")
    void testGetUserById_NotFound() throws Exception {
        // Given
        when(userService.getUserById(999L))
                .thenThrow(new IllegalArgumentException("User with ID 999 not found"));

        // When & Then
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("User with ID 999 not found"));
    }

    // ========== GET ALL USERS TESTS ==========

    @Test
    @DisplayName("GET /api/users - Should return all users successfully")
    void testGetAllUsers_Success() throws Exception {
        // Given
        User user1 = new User("John Doe", "john@example.com", 30);
        user1.setId(1L);
        User user2 = new User("Jane Smith", "jane@example.com", 25);
        user2.setId(2L);
        List<User> users = Arrays.asList(user1, user2);

        UserDTO dto1 = new UserDTO(1L, "John Doe", "john@example.com", 30, LocalDateTime.now());
        UserDTO dto2 = new UserDTO(2L, "Jane Smith", "jane@example.com", 25, LocalDateTime.now());
        List<UserDTO> dtos = Arrays.asList(dto1, dto2);

        when(userService.getAllUsers()).thenReturn(users);
        when(userMapper.toDTOList(users)).thenReturn(dtos);

        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Jane Smith"));

        verify(userService, times(1)).getAllUsers();
        verify(userMapper, times(1)).toDTOList(users);
    }

    @Test
    @DisplayName("GET /api/users - Should return empty list when no users exist")
    void testGetAllUsers_EmptyList() throws Exception {
        // Given
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());
        when(userMapper.toDTOList(anyList())).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(userService, times(1)).getAllUsers();
    }

    // ========== UPDATE USER TESTS ==========

    @Test
    @DisplayName("PUT /api/users/{id} - Should update user successfully")
    void testUpdateUser_Success() throws Exception {
        // Given
        UserUpdateDTO updateDTO = new UserUpdateDTO("John Updated", "john.updated@example.com", 31);
        User updatedUser = new User("John Updated", "john.updated@example.com", 31);
        updatedUser.setId(1L);
        updatedUser.setCreatedAt(LocalDateTime.now());
        UserDTO responseDTO = new UserDTO(1L, "John Updated", "john.updated@example.com", 31, updatedUser.getCreatedAt());

        when(userService.getUserById(1L)).thenReturn(testUser);
        doNothing().when(userMapper).updateEntityFromDTO(any(User.class), any(UserUpdateDTO.class));
        when(userService.updateUser(eq(1L), anyString(), anyString(), anyInt())).thenReturn(updatedUser);
        when(userMapper.toDTO(any(User.class))).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Updated"))
                .andExpect(jsonPath("$.email").value("john.updated@example.com"))
                .andExpect(jsonPath("$.age").value(31));

        verify(userService, times(1)).getUserById(1L);
        verify(userMapper, times(1)).updateEntityFromDTO(any(User.class), any(UserUpdateDTO.class));
        verify(userService, times(1)).updateUser(eq(1L), anyString(), anyString(), anyInt());
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Should update user partially")
    void testUpdateUser_PartialUpdate() throws Exception {
        // Given
        UserUpdateDTO updateDTO = new UserUpdateDTO("John Updated", null, null);
        User updatedUser = new User("John Updated", "john@example.com", 30);
        updatedUser.setId(1L);
        updatedUser.setCreatedAt(LocalDateTime.now());
        UserDTO responseDTO = new UserDTO(1L, "John Updated", "john@example.com", 30, updatedUser.getCreatedAt());

        when(userService.getUserById(1L)).thenReturn(testUser);
        doNothing().when(userMapper).updateEntityFromDTO(any(User.class), any(UserUpdateDTO.class));
        when(userService.updateUser(eq(1L), anyString(), anyString(), anyInt())).thenReturn(updatedUser);
        when(userMapper.toDTO(any(User.class))).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Updated"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.age").value(30));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Should return 400 when id is invalid")
    void testUpdateUser_InvalidId() throws Exception {
        // Given
        UserUpdateDTO updateDTO = new UserUpdateDTO("John Updated", null, null);

        // When & Then
        mockMvc.perform(put("/api/users/0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).getUserById(anyLong());
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Should return 400 when user not found")
    void testUpdateUser_NotFound() throws Exception {
        // Given
        UserUpdateDTO updateDTO = new UserUpdateDTO("John Updated", null, null);
        when(userService.getUserById(999L))
                .thenThrow(new IllegalArgumentException("User with ID 999 not found"));

        // When & Then
        mockMvc.perform(put("/api/users/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("User with ID 999 not found"));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Should return 400 when email is invalid")
    void testUpdateUser_InvalidEmail() throws Exception {
        // Given
        UserUpdateDTO updateDTO = new UserUpdateDTO(null, "invalid-email", null);

        // When & Then
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(anyLong(), anyString(), anyString(), anyInt());
    }

    // ========== DELETE USER TESTS ==========

    @Test
    @DisplayName("DELETE /api/users/{id} - Should delete user successfully")
    void testDeleteUser_Success() throws Exception {
        // Given
        doNothing().when(userService).deleteUser(1L);

        // When & Then
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Should return 400 when id is invalid")
    void testDeleteUser_InvalidId() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/users/0"))
                .andExpect(status().isBadRequest());

        verify(userService, never()).deleteUser(anyLong());
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Should return 400 when user not found")
    void testDeleteUser_NotFound() throws Exception {
        // Given
        doThrow(new IllegalArgumentException("User with ID 999 not found"))
                .when(userService).deleteUser(999L);

        // When & Then
        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("User with ID 999 not found"));
    }
}

