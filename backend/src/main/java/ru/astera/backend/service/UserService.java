package ru.astera.backend.service;

import org.springframework.stereotype.Service;
import ru.astera.backend.dto.registration.CustomerRegistrationDto;
import ru.astera.backend.dto.registration.ManagerRegistrationDto;
import ru.astera.backend.entity.User;

@Service
public interface UserService {
    User createManager(ManagerRegistrationDto dto);
    User createCustomer(CustomerRegistrationDto dto);
    boolean existsByEmail(String email);
    User findByEmail(String email);
    boolean checkPassword(User user, String rawPassword);
}