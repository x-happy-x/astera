package ru.astera.backend.service;

import ru.astera.backend.dto.registration.CustomerRegistrationDto;
import ru.astera.backend.dto.registration.ManagerRegistrationDto;
import ru.astera.backend.entity.User;

public interface UserService {
    User createManager(ManagerRegistrationDto dto);

    User createCustomer(CustomerRegistrationDto dto);

    boolean existsByEmail(String email);

    User findByEmail(String email);

    boolean checkPassword(User user, String rawPassword);
}