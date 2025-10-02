package ru.astera.backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.astera.backend.dto.registration.ClientRegistrationDto;
import ru.astera.backend.entity.CustomerProfile;
import ru.astera.backend.entity.User;
import ru.astera.backend.exception.LeadAlreadyExistsException;
import ru.astera.backend.repository.CustomerProfileRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final UserService userService;
    private final CustomerProfileRepository profileRepository;

    @Transactional
    public CustomerProfile registerCustomer(ClientRegistrationDto dto) {
        if (userService.existsByEmail(dto.getEmail())) {
            throw new LeadAlreadyExistsException("Пользователь с такой почтой уже зарегистрирован");
        }

        User user = userService.createCustomer(dto);

        CustomerProfile profile = new CustomerProfile();
        profile.setUser(user);
        profile.setPhone(dto.getPhone());
        profile.setOrganization(dto.getOrganization());

        return profileRepository.save(profile);
    }

    public CustomerProfile findCustomerByUserId(UUID userId) {
        return profileRepository.findCustomerProfileByUserId(userId);
    }
}