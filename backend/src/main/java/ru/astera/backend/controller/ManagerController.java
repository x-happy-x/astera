package ru.astera.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.astera.backend.dto.ManagerRegistrationDto;
import ru.astera.backend.entity.User;
import ru.astera.backend.service.UserService;

@RestController
@RequestMapping("/api/admin/managers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('admin')")
public class ManagerController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> registerManager(@Valid @RequestBody ManagerRegistrationDto dto) {
        User manager = userService.createManager(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(manager);
    }
}