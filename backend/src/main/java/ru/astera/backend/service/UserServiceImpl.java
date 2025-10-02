package ru.astera.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.astera.backend.dto.registration.CustomerRegistrationDto;
import ru.astera.backend.dto.registration.ManagerRegistrationDto;
import ru.astera.backend.entity.User;
import ru.astera.backend.exception.UserAlreadyExistsException;
import ru.astera.backend.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
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

    @Override
    public User createCustomer(CustomerRegistrationDto dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setFullName(dto.getFullName());
        user.setRole(User.Role.customer);
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setIsActive(true);
        return userRepository.save(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email).orElse(null);
    }

    @Override
    public boolean checkPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPasswordHash());
    }
}