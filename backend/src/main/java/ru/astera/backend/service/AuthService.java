package ru.astera.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.astera.backend.dto.registration.*;
import ru.astera.backend.entity.CustomerProfile;
import ru.astera.backend.entity.User;
import ru.astera.backend.exception.InvalidCredentialsException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final CustomerService customerService;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    public AuthResponseDto login(LoginDto dto) {
        User user = loginUser(dto);
        if (user.getRole() == User.Role.customer) {
            throw new InvalidCredentialsException("Неверные учетные данные");
        }
        return createResponse(user);
    }

    public AuthResponseDto register(ManagerRegistrationDto dto) {
        User user = userService.createManager(dto);
        return createResponse(user);
    }

    public CustomerResponseDto loginCustomer(LoginDto dto) {
        User user = loginUser(dto);
        if (user.getRole() != User.Role.customer) {
            throw new InvalidCredentialsException("Неверные учетные данные");
        }

        CustomerProfile profile = customerService.findCustomerByUserId(user.getId());

        return createCustomerResponse(user, profile);
    }

    public CustomerResponseDto registerCustomer(ClientRegistrationDto dto) {
        CustomerProfile profile = customerService.registerCustomer(dto);
        User user = profile.getUser();

        return createCustomerResponse(user, profile);
    }

    private User loginUser(LoginDto dto) {
        User user = userService.findByEmail(dto.getEmail());
        if (user == null || !user.getIsActive()) {
            throw new InvalidCredentialsException("Неверные учетные данные");
        }
        if (user.getPasswordHash() == null || !userService.checkPassword(user, dto.getPassword())) {
            throw new InvalidCredentialsException("Неверные учетные данные");
        }
        return user;
    }

    private AuthResponseDto createResponse(User user) {
        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponseDto(user.getId(), token, user.getEmail(), user.getFullName(), user.getRole());
    }

    private CustomerResponseDto createCustomerResponse(User user, CustomerProfile profile) {
        AuthResponseDto authResponse = createResponse(user);

        CustomerResponseDto customerResponse = objectMapper.convertValue(authResponse, CustomerResponseDto.class);
        customerResponse.setPhone(profile.getPhone());
        customerResponse.setOrganization(profile.getOrganization());

        return customerResponse;
    }
}