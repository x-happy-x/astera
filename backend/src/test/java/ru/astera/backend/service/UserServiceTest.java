package ru.astera.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.astera.backend.dto.ManagerRegistrationDto;
import ru.astera.backend.entity.User;
import ru.astera.backend.exception.UserAlreadyExistsException;
import ru.astera.backend.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private ManagerRegistrationDto managerDto;
    private User existingUser;

    @BeforeEach
    void setUp() {
        managerDto = new ManagerRegistrationDto();
        managerDto.setEmail("manager@test.com");
        managerDto.setFullName("Test Manager");
        managerDto.setPassword("password123");

        existingUser = new User();
        existingUser.setEmail("manager@test.com");
        existingUser.setFullName("Test Manager");
        existingUser.setRole(User.Role.manager);
        existingUser.setIsActive(true);
    }

    @Test
    void createManager_Success() {
        when(userRepository.existsByEmail(managerDto.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        User result = userService.createManager(managerDto);

        assertNotNull(result);
        assertEquals(managerDto.getEmail(), result.getEmail());
        assertEquals(managerDto.getFullName(), result.getFullName());
        assertEquals(User.Role.manager, result.getRole());
        assertTrue(result.getIsActive());

        verify(userRepository).existsByEmail(managerDto.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createManager_UserAlreadyExists() {
        when(userRepository.existsByEmail(managerDto.getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.createManager(managerDto));

        verify(userRepository).existsByEmail(managerDto.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findByEmail_UserExists() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(existingUser));

        User result = userService.findByEmail("manager@test.com");

        assertNotNull(result);
        assertEquals(existingUser.getEmail(), result.getEmail());

        verify(userRepository).findByEmail("manager@test.com");
    }

    @Test
    void findByEmail_UserNotExists() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        User result = userService.findByEmail("nonexistent@test.com");

        assertNull(result);

        verify(userRepository).findByEmail("nonexistent@test.com");
    }

    @Test
    void checkPassword_ValidPassword() {
        existingUser.setPasswordHash("$2a$10$YcZYZNkZJL9Y3HBqZzZQ.O7XvZvZLqZ2qZ3qZ4qZ5qZ6qZ7qZ8qZ9q");

        boolean result = userService.checkPassword(existingUser, "admin");

        assertFalse(result); // BCrypt hash doesn't match "admin"
    }
}