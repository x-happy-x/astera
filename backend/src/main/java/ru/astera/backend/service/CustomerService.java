package ru.astera.backend.service;

import ru.astera.backend.dto.admin.CustomerCreateDto;
import ru.astera.backend.dto.admin.CustomerDto;
import ru.astera.backend.dto.admin.CustomerPageDto;
import ru.astera.backend.dto.admin.CustomerUpdateDto;
import ru.astera.backend.dto.registration.CustomerRegistrationDto;
import ru.astera.backend.entity.CustomerProfile;

import java.util.UUID;

public interface CustomerService {
    CustomerProfile registerCustomer(CustomerRegistrationDto dto);

    CustomerProfile findCustomerByUserId(UUID userId);

    CustomerPageDto getCustomersWithPagination(int page, int size);

    CustomerDto getCustomerById(UUID customerId);

    CustomerDto createCustomer(CustomerCreateDto dto);

    CustomerDto updateCustomer(UUID customerId, CustomerUpdateDto dto);

    void deleteCustomer(UUID customerId);
}