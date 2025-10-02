package ru.astera.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.astera.backend.dto.admin.CustomerCreateDto;
import ru.astera.backend.dto.admin.CustomerDto;
import ru.astera.backend.dto.admin.CustomerPageDto;
import ru.astera.backend.dto.admin.CustomerUpdateDto;
import ru.astera.backend.service.CustomerService;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/customers")
@RequiredArgsConstructor
public class AdminController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<CustomerPageDto> getCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        CustomerPageDto customers = customerService.getCustomersWithPagination(page, size);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDto> getCustomer(@PathVariable UUID id) {
        CustomerDto customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }

    @PostMapping
    public ResponseEntity<CustomerDto> createCustomer(@Valid @RequestBody CustomerCreateDto dto) {
        CustomerDto customer = customerService.createCustomer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDto> updateCustomer(
            @PathVariable UUID id,
            @Valid @RequestBody CustomerUpdateDto dto) {
        CustomerDto customer = customerService.updateCustomer(id, dto);
        return ResponseEntity.ok(customer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}