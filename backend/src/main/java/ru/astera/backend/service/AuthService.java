package ru.astera.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.astera.backend.dto.AuthResponseDto;
import ru.astera.backend.dto.LoginDto;
import ru.astera.backend.entity.User;
import ru.astera.backend.exception.InvalidCredentialsException;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserService userService;
    private final JwtService jwtService;
    
    public AuthResponseDto login(LoginDto dto) {
        User user = userService.findByEmail(dto.getEmail());
        
        if (user == null || !user.getIsActive()) {
            throw new InvalidCredentialsException("Неверные учетные данные");
        }
        
        if (user.getPasswordHash() == null || !userService.checkPassword(user, dto.getPassword())) {
            throw new InvalidCredentialsException("Неверные учетные данные");
        }
        
        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        
        return new AuthResponseDto(token, user.getEmail(), user.getFullName(), user.getRole());
    }
}