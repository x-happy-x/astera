package ru.astera.backend.service;

import ru.astera.backend.dto.registration.*;

public interface AuthService {
    AuthResponseDto login(LoginDto dto);

    AuthResponseDto register(ManagerRegistrationDto dto);

    CustomerResponseDto loginCustomer(LoginDto dto);

    CustomerResponseDto registerCustomer(CustomerRegistrationDto dto);
}