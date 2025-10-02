package ru.astera.backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.astera.backend.dto.admin.CustomerCreateDto;
import ru.astera.backend.dto.admin.CustomerDto;
import ru.astera.backend.dto.admin.CustomerPageDto;
import ru.astera.backend.dto.admin.CustomerUpdateDto;
import ru.astera.backend.dto.registration.CustomerRegistrationDto;
import ru.astera.backend.entity.CustomerProfile;
import ru.astera.backend.entity.User;
import ru.astera.backend.exception.CustomerAlreadyExistsException;
import ru.astera.backend.exception.CustomerNotFoundException;
import ru.astera.backend.exception.UserAlreadyExistsException;
import ru.astera.backend.repository.CustomerProfileRepository;
import ru.astera.backend.repository.UserRepository;
import ru.astera.backend.service.CustomerService;
import ru.astera.backend.service.UserService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final UserService userService;
    private final CustomerProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public CustomerProfile registerCustomer(CustomerRegistrationDto dto) {
        if (userService.existsByEmail(dto.getEmail())) {
            throw new CustomerAlreadyExistsException("Пользователь с такой почтой уже зарегистрирован");
        }

        User user = userService.createCustomer(dto);

        CustomerProfile profile = new CustomerProfile();
        profile.setUser(user);
        profile.setPhone(dto.getPhone());
        profile.setOrganization(dto.getOrganization());

        return profileRepository.save(profile);
    }

    @Override
    public CustomerProfile findCustomerByUserId(UUID userId) {
        return profileRepository.findCustomerProfileByUserId(userId);
    }

    @Override
    public CustomerPageDto getCustomersWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CustomerProfile> customerPage = profileRepository.findAllCustomersWithUsers(pageable);

        CustomerPageDto result = new CustomerPageDto();
        result.setCustomers(customerPage.getContent().stream()
                .map(this::mapToDto)
                .toList());
        result.setTotalCustomers(customerPage.getTotalElements());
        result.setCurrentPage(customerPage.getNumber());
        result.setTotalPages(customerPage.getTotalPages());
        result.setPageSize(customerPage.getSize());

        return result;
    }

    @Override
    public CustomerDto getCustomerById(UUID customerId) {
        CustomerProfile profile = profileRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Клиент не найден"));
        return mapToDto(profile);
    }

    @Override
    @Transactional
    public CustomerDto createCustomer(CustomerCreateDto dto) {
        CustomerRegistrationDto registrationDto = objectMapper.convertValue(dto, CustomerRegistrationDto.class);
        return mapToDto(registerCustomer(registrationDto));
    }

    @Override
    @Transactional
    public CustomerDto updateCustomer(UUID customerId, CustomerUpdateDto dto) {
        CustomerProfile profile = profileRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));

        User user = profile.getUser();

        if (!user.getEmail().equals(dto.getEmail()) && userService.existsByEmail(dto.getEmail())) {
            throw new UserAlreadyExistsException("Пользователь с таким email уже существует");
        }

        user.setEmail(dto.getEmail());
        user.setFullName(dto.getFullName());
        user.setIsActive(dto.getIsActive());

        profile.setPhone(dto.getPhone());
        profile.setOrganization(dto.getOrganization());

        userRepository.save(user);
        profile = profileRepository.save(profile);

        return mapToDto(profile);
    }

    @Override
    @Transactional
    public void deleteCustomer(UUID customerId) {
        CustomerProfile profile = profileRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));

        User user = profile.getUser();
        profileRepository.delete(profile);
        userRepository.delete(user);
    }

    private CustomerDto mapToDto(CustomerProfile profile) {
        CustomerDto dto = new CustomerDto();
        dto.setId(profile.getUserId());
        dto.setEmail(profile.getUser().getEmail());
        dto.setFullName(profile.getUser().getFullName());
        dto.setPhone(profile.getPhone());
        dto.setOrganization(profile.getOrganization());
        dto.setIsActive(profile.getUser().getIsActive());
        dto.setCreatedAt(profile.getCreatedAt());
        return dto;
    }
}