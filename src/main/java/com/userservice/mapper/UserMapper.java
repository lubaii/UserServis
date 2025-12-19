package com.userservice.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.userservice.dto.UserCreateDTO;
import com.userservice.dto.UserDTO;
import com.userservice.dto.UserUpdateDTO;
import com.userservice.entity.User;

/**
 * Маппер для конвертации между Entity и DTO.
 */
@Component
public class UserMapper {

    /**
     * Конвертирует Entity в DTO для ответа.
     */
    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        return new UserDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getAge(),
            user.getCreatedAt()
        );
    }

    /**
     * Конвертирует список Entity в список DTO.
     */
    public List<UserDTO> toDTOList(List<User> users) {
        if (users == null) {
            return null;
        }
        return users.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Конвертирует DTO для создания в Entity.
     */
    public User toEntity(UserCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        return new User(
            dto.getName(),
            dto.getEmail(),
            dto.getAge()
        );
    }

    /**
     * Обновляет Entity данными из DTO для обновления.
     * Обновляются только не-null поля из DTO.
     */
    public void updateEntityFromDTO(User user, UserUpdateDTO dto) {
        if (user == null || dto == null) {
            return;
        }
        
        if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
            user.setName(dto.getName().trim());
        }
        
        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            user.setEmail(dto.getEmail().trim());
        }
        
        if (dto.getAge() != null) {
            user.setAge(dto.getAge());
        }
    }
}

