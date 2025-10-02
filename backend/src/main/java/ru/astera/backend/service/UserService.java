package ru.astera.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.astera.backend.dto.registration.ClientRegistrationDto;
import ru.astera.backend.dto.registration.ManagerRegistrationDto;
import ru.astera.backend.entity.User;
import ru.astera.backend.exception.UserAlreadyExistsException;
import ru.astera.backend.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createManager(ManagerRegistrationDto dto) {
        if (existsByEmail(dto.getEmail())) {
            throw new UserAlreadyExistsException("Пользователь с таким email уже существует");
        }

        User manager = new User();
        manager.setEmail(dto.getEmail());
        manager.setFullName(dto.getFullName());
        manager.setRole(User.Role.manager);
        manager.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        manager.setIsActive(true);

        return userRepository.save(manager);
    }

    public User createCustomer(ClientRegistrationDto dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setFullName(dto.getFullName());
        user.setRole(User.Role.customer);
        user.setIsActive(true);
        return userRepository.save(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email).orElse(null);
    }

    public boolean checkPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPasswordHash());
    }
}