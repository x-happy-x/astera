package ru.astera.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.astera.backend.dto.registration.*;
import ru.astera.backend.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/customer/register")
    public ResponseEntity<CustomerResponseDto> registerCustomer(@Valid @RequestBody ClientRegistrationDto dto) {
        CustomerResponseDto response = authService.registerCustomer(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/customer/login")
    public ResponseEntity<CustomerResponseDto> registerCustomer(@Valid @RequestBody LoginDto dto) {
        CustomerResponseDto response = authService.loginCustomer(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/manager/register")
    public ResponseEntity<AuthResponseDto> registerManager(@Valid @RequestBody ManagerRegistrationDto dto) {
        AuthResponseDto response = authService.register(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginDto dto) {
        AuthResponseDto response = authService.login(dto);
        return ResponseEntity.ok(response);
    }
}