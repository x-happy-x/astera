package ru.astera.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.astera.backend.dto.LeadRegistrationDto;
import ru.astera.backend.entity.Lead;
import ru.astera.backend.service.LeadService;

@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
public class LeadController {

    private final LeadService leadService;

    @PostMapping("/register")
    public ResponseEntity<Lead> registerLead(@Valid @RequestBody LeadRegistrationDto dto) {
        Lead lead = leadService.createLead(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(lead);
    }
}