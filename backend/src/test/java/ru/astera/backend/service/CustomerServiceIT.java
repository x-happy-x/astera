package ru.astera.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.astera.backend.config.TestContainersConfig;
import ru.astera.backend.dto.registration.ClientRegistrationDto;
import ru.astera.backend.entity.CustomerProfile;
import ru.astera.backend.entity.User;
import ru.astera.backend.exception.LeadAlreadyExistsException;
import ru.astera.backend.repository.CustomerProfileRepository;
import ru.astera.backend.repository.UserRepository;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestContainersConfig.class)
@Transactional
class CustomerServiceIT {

    @Autowired
    CustomerService customerService;
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CustomerProfileRepository profileRepository;

    @BeforeEach
    void clean() {
        profileRepository.deleteAll();
        userRepository.deleteAll();
    }

    private ClientRegistrationDto sampleDto(String email) {
        return ClientRegistrationDto.builder()
                .fullName("Иван Иванов")
                .phone("+7 900 123-45-67")
                .email(email)
                .organization("ООО Ромашка")
                .password("password123")
                .build();
    }

    @Test
    @DisplayName("При сохранении клиента создается User и CustomerProfile")
    @Transactional
    void registerCustomer_createsUserAndProfile() {
        var dto = sampleDto("new@ex.com");

        CustomerProfile profile = customerService.registerCustomer(dto);

        assertThat(profile).isNotNull();
        assertThat(profile.getOrganization()).isEqualTo("ООО Ромашка");
        assertThat(profile.getPhone()).isEqualTo("+7 900 123-45-67");

        User user = profile.getUser();
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo("new@ex.com");
        assertThat(user.getFullName()).isEqualTo("Иван Иванов");
        assertThat(user.getRole()).isEqualTo(User.Role.customer);
        assertThat(user.getIsActive()).isTrue();

        assertThat(profileRepository.findCustomerProfileByUserId(user.getId())).isNotNull();
        assertThat(userRepository.findByEmailIgnoreCase("new@ex.com")).isPresent();
    }

    @Test
    @DisplayName("При сохранении клиента выбрасывается LeadAlreadyExistsException если email уже занят")
    void registerCustomer_throwsWhenEmailExists() {
        userService.createCustomer(sampleDto("dup@ex.com"));

        assertThatThrownBy(() -> customerService.registerCustomer(sampleDto("dup@ex.com")))
                .isInstanceOf(LeadAlreadyExistsException.class)
                .hasMessageContaining("Пользователь с такой почтой уже зарегистрирован");
    }

    @Test
    @DisplayName("Поиск клиента по userId корректно работает")
    void findCustomerByUserId_returnsProfile() {
        var dto = sampleDto("findme@ex.com");
        var createdProfile = customerService.registerCustomer(dto);

        UUID userId = createdProfile.getUser().getId();

        CustomerProfile found = customerService.findCustomerByUserId(userId);

        assertThat(found).isNotNull();
        assertThat(found.getUser().getId()).isEqualTo(userId);
    }
}