package ru.astera.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.astera.backend.dto.AuthResponseDto;
import ru.astera.backend.dto.LoginDto;
import ru.astera.backend.entity.User;
import ru.astera.backend.exception.InvalidCredentialsException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private LoginDto loginDto;
    private User user;

    @BeforeEach
    void setUp() {
        loginDto = new LoginDto();
        loginDto.setEmail("admin@example.com");
        loginDto.setPassword("admin");

        user = new User();
        user.setEmail("admin@example.com");
        user.setFullName("Administrator");
        user.setRole(User.Role.admin);
        user.setPasswordHash("$2a$10$YcZYZNkZJL9Y3HBqZzZQ.O7XvZvZLqZ2qZ3qZ4qZ5qZ6qZ7qZ8qZ9q");
        user.setIsActive(true);
    }

    @Test
    void login_Success() {
        when(userService.findByEmail(loginDto.getEmail())).thenReturn(user);
        when(userService.checkPassword(user, loginDto.getPassword())).thenReturn(true);
        when(jwtService.generateToken(user.getEmail(), user.getRole().name())).thenReturn("test-token");

        AuthResponseDto result = authService.login(loginDto);

        assertNotNull(result);
        assertEquals("test-token", result.getToken());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getFullName(), result.getFullName());
        assertEquals(user.getRole(), result.getRole());

        verify(userService).findByEmail(loginDto.getEmail());
        verify(userService).checkPassword(user, loginDto.getPassword());
        verify(jwtService).generateToken(user.getEmail(), user.getRole().name());
    }

    @Test
    void login_UserNotFound() {
        when(userService.findByEmail(loginDto.getEmail())).thenReturn(null);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(loginDto));

        verify(userService).findByEmail(loginDto.getEmail());
        verify(userService, never()).checkPassword(any(), anyString());
        verify(jwtService, never()).generateToken(anyString(), anyString());
    }

    @Test
    void login_UserNotActive() {
        user.setIsActive(false);
        when(userService.findByEmail(loginDto.getEmail())).thenReturn(user);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(loginDto));

        verify(userService).findByEmail(loginDto.getEmail());
        verify(userService, never()).checkPassword(any(), anyString());
        verify(jwtService, never()).generateToken(anyString(), anyString());
    }

    @Test
    void login_InvalidPassword() {
        when(userService.findByEmail(loginDto.getEmail())).thenReturn(user);
        when(userService.checkPassword(user, loginDto.getPassword())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(loginDto));

        verify(userService).findByEmail(loginDto.getEmail());
        verify(userService).checkPassword(user, loginDto.getPassword());
        verify(jwtService, never()).generateToken(anyString(), anyString());
    }

    @Test
    void login_UserWithoutPassword() {
        user.setPasswordHash(null);
        when(userService.findByEmail(loginDto.getEmail())).thenReturn(user);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(loginDto));

        verify(userService).findByEmail(loginDto.getEmail());
        verify(userService, never()).checkPassword(any(), anyString());
        verify(jwtService, never()).generateToken(anyString(), anyString());
    }
}