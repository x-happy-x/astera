package ru.astera.backend.service;

import org.springframework.stereotype.Service;
import ru.astera.backend.dto.registration.*;

@Service
public interface AuthService {
    AuthResponseDto login(LoginDto dto);

    AuthResponseDto register(ManagerRegistrationDto dto);

    CustomerResponseDto loginCustomer(LoginDto dto);

    CustomerResponseDto registerCustomer(CustomerRegistrationDto dto);
}