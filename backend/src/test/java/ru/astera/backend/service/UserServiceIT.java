package ru.astera.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.astera.backend.config.TestContainersConfig;
import ru.astera.backend.dto.registration.ClientRegistrationDto;
import ru.astera.backend.dto.registration.ManagerRegistrationDto;
import ru.astera.backend.entity.User;
import ru.astera.backend.exception.UserAlreadyExistsException;
import ru.astera.backend.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestContainersConfig.class)
class UserServiceIT {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
    }

    private ManagerRegistrationDto managerDto(String email, String pass) {
        var dto = new ManagerRegistrationDto();
        dto.setEmail(email);
        dto.setFullName("Менеджер Тестов");
        dto.setPassword(pass);
        return dto;
    }

    private ClientRegistrationDto customerDto(String email) {
        return ClientRegistrationDto.builder()
                .email(email)
                .fullName("Клиент Клиентов")
                .organization("ООО Клиент")
                .phone("+7 900 000-00-00")
                .password("customerPass!")
                .build();
    }

    @Test
    @DisplayName("При создании менеджера сохраняется пользователь с ролью manager и захешированным паролем")
    void createManager_persistsManagerWithEncodedPassword() {
        User m = userService.createManager(managerDto("mgr@ex.com", "S3cret!"));

        assertThat(m.getId()).isNotNull();
        assertThat(m.getEmail()).isEqualTo("mgr@ex.com");
        assertThat(m.getFullName()).isEqualTo("Менеджер Тестов");
        assertThat(m.getRole()).isEqualTo(User.Role.manager);
        assertThat(m.getIsActive()).isTrue();
        assertThat(m.getPasswordHash())
                .isNotBlank()
                .isNotEqualTo("S3cret!");

        assertThat(userRepository.findByEmailIgnoreCase("mgr@ex.com")).isPresent();
    }

    @Test
    @DisplayName("При создании менеджера выбрасывается UserAlreadyExistsException при дубликате email")
    void createManager_throwsOnDuplicateEmail() {
        userService.createManager(managerDto("dup@ex.com", "qwe12345"));
        assertThatThrownBy(() -> userService.createManager(managerDto("dup@ex.com", "qwe12345")))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("Пользователь с таким email уже существует");
    }

    @Test
    @DisplayName("При создании клиента сохраняет пользователя с ролью customer")
    void createCustomer_persistsCustomer() {
        User c = userService.createCustomer(customerDto("cust@ex.com"));

        assertThat(c.getId()).isNotNull();
        assertThat(c.getEmail()).isEqualTo("cust@ex.com");
        assertThat(c.getRole()).isEqualTo(User.Role.customer);
        assertThat(c.getIsActive()).isTrue();

        assertThat(userRepository.findByEmailIgnoreCase("cust@ex.com")).isPresent();
    }

    @Test
    @DisplayName("Возвращает true/false в зависимости от наличия пользователя с таким email")
    void existsByEmail_works() {
        assertThat(userService.existsByEmail("nobody@ex.com")).isFalse();
        userService.createCustomer(customerDto("exists@ex.com"));
        assertThat(userService.existsByEmail("exists@ex.com")).isTrue();
    }

    @Test
    @DisplayName("Находит пользователя по email (без учета регистра)")
    void findByEmail_works() {
        assertThat(userService.findByEmail("absent@ex.com")).isNull();

        userService.createCustomer(customerDto("found@ex.com"));
        User found = userService.findByEmail("FoUnD@ex.com");
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("found@ex.com");
    }

    @Test
    @DisplayName("Возвращает true для корректного пароля и false для некорректного")
    void checkPassword_trueAndFalse() {
        User m = userService.createManager(managerDto("chk@ex.com", "RightPass1"));
        assertThat(userService.checkPassword(m, "RightPass1")).isTrue();
        assertThat(userService.checkPassword(m, "WrongPass")).isFalse();
    }
}
