package ru.astera.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.astera.backend.dto.registration.*;
import ru.astera.backend.entity.CustomerProfile;
import ru.astera.backend.entity.User;
import ru.astera.backend.exception.InvalidCredentialsException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static ru.astera.backend.entity.User.Role.customer;
import static ru.astera.backend.entity.User.Role.manager;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    UserService userService;
    @Mock
    CustomerService customerService;
    @Mock
    JwtService jwtService;
    @Mock
    ObjectMapper objectMapper;

    @InjectMocks
    AuthService authService;

    private final UUID USER_ID = UUID.randomUUID();

    private User user(User.Role role, boolean active, String email, String fullName, String passwordHash) {
        User u = new User();
        u.setId(USER_ID);
        u.setRole(role);
        u.setIsActive(active);
        u.setEmail(email);
        u.setFullName(fullName);
        u.setPasswordHash(passwordHash);
        return u;
    }

    @Nested
    @DisplayName("login (менеджеры)")
    class LoginManager {

        @Test
        @DisplayName("успех: менеджер, активен, пароль ок → возвращает токен и данные")
        void login_success_manager() {
            var dto = new LoginDto();
            dto.setEmail("mgr@ex.com");
            dto.setPassword("pass");

            var mgr = user(manager, true, "mgr@ex.com", "Менеджер", "$hash$");
            when(userService.findByEmail("mgr@ex.com")).thenReturn(mgr);
            when(userService.checkPassword(mgr, "pass")).thenReturn(true);
            when(jwtService.generateToken("mgr@ex.com", "manager")).thenReturn("jwt-token");

            AuthResponseDto resp = authService.login(dto);

            assertThat(resp).isNotNull();
            assertThat(resp.getId()).isEqualTo(USER_ID);
            assertThat(resp.getToken()).isEqualTo("jwt-token");
            assertThat(resp.getEmail()).isEqualTo("mgr@ex.com");
            assertThat(resp.getFullName()).isEqualTo("Менеджер");
            assertThat(resp.getRole()).isEqualTo(manager);
        }

        @Test
        @DisplayName("ошибка: найден customer → InvalidCredentialsException")
        void login_rejects_customer() {
            var dto = new LoginDto();
            dto.setEmail("cust@ex.com");
            dto.setPassword("pass");

            var cust = user(customer, true, "cust@ex.com", "Клиент", "$hash$");
            when(userService.findByEmail("cust@ex.com")).thenReturn(cust);
            when(userService.checkPassword(cust, "pass")).thenReturn(true);

            assertThatThrownBy(() -> authService.login(dto))
                    .isInstanceOf(InvalidCredentialsException.class);
            verify(jwtService, never()).generateToken(any(), any());
        }

        @Test
        @DisplayName("ошибка: пользователь не найден → InvalidCredentialsException")
        void login_user_not_found() {
            var dto = new LoginDto();
            dto.setEmail("absent@ex.com");
            dto.setPassword("pass");

            when(userService.findByEmail("absent@ex.com")).thenReturn(null);

            assertThatThrownBy(() -> authService.login(dto))
                    .isInstanceOf(InvalidCredentialsException.class);
        }

        @Test
        @DisplayName("ошибка: пользователь неактивен → InvalidCredentialsException")
        void login_user_inactive() {
            var dto = new LoginDto();
            dto.setEmail("inactive@ex.com");
            dto.setPassword("pass");

            var u = user(manager, false, "inactive@ex.com", "X", "$hash$");
            when(userService.findByEmail("inactive@ex.com")).thenReturn(u);

            assertThatThrownBy(() -> authService.login(dto))
                    .isInstanceOf(InvalidCredentialsException.class);
        }

        @Test
        @DisplayName("ошибка: пустой hash → InvalidCredentialsException")
        void login_user_no_password_hash() {
            var dto = new LoginDto();
            dto.setEmail("mgr@ex.com");
            dto.setPassword("pass");

            var u = user(manager, true, "mgr@ex.com", "X", null); // passwordHash = null
            when(userService.findByEmail("mgr@ex.com")).thenReturn(u);

            assertThatThrownBy(() -> authService.login(dto))
                    .isInstanceOf(InvalidCredentialsException.class);
        }

        @Test
        @DisplayName("ошибка: пароль неверный → InvalidCredentialsException")
        void login_wrong_password() {
            var dto = new LoginDto();
            dto.setEmail("mgr@ex.com");
            dto.setPassword("wrong");

            var u = user(manager, true, "mgr@ex.com", "X", "$hash$");
            when(userService.findByEmail("mgr@ex.com")).thenReturn(u);
            when(userService.checkPassword(u, "wrong")).thenReturn(false);

            assertThatThrownBy(() -> authService.login(dto))
                    .isInstanceOf(InvalidCredentialsException.class);
        }
    }

    @Nested
    @DisplayName("register (менеджеры)")
    class RegisterManager {

        @Test
        @DisplayName("успех: создаёт менеджера и возвращает токен")
        void register_success() {
            var dto = new ManagerRegistrationDto();
            dto.setEmail("mgr@ex.com");
            dto.setFullName("М1");
            dto.setPassword("P@ss");

            var user = user(manager, true, "mgr@ex.com", "М1", "$hash$");
            when(userService.createManager(dto)).thenReturn(user);
            when(jwtService.generateToken("mgr@ex.com", "manager")).thenReturn("jwt-token");

            AuthResponseDto resp = authService.register(dto);

            assertThat(resp.getId()).isEqualTo(USER_ID);
            assertThat(resp.getToken()).isEqualTo("jwt-token");
            assertThat(resp.getRole()).isEqualTo(manager);
        }
    }

    @Nested
    @DisplayName("loginCustomer (клиенты)")
    class LoginCustomer {

        @Test
        @DisplayName("успех: customer + профиль → CustomerResponseDto (token + phone/org)")
        void login_customer_success() {
            var dto = new LoginDto();
            dto.setEmail("cust@ex.com");
            dto.setPassword("pass");

            var cust = user(customer, true, "cust@ex.com", "Клиент", "$hash$");
            when(userService.findByEmail("cust@ex.com")).thenReturn(cust);
            when(userService.checkPassword(cust, "pass")).thenReturn(true);

            var profile = new CustomerProfile();
            profile.setUser(cust);
            profile.setPhone("+7 900 000-00-00");
            profile.setOrganization("ООО Ромашка");
            when(customerService.findCustomerByUserId(USER_ID)).thenReturn(profile);

            when(jwtService.generateToken("cust@ex.com", "customer")).thenReturn("cust-token");
            when(objectMapper.convertValue(any(AuthResponseDto.class), eq(CustomerResponseDto.class)))
                    .thenAnswer(inv -> {
                        AuthResponseDto base = inv.getArgument(0);
                        CustomerResponseDto out = new CustomerResponseDto();
                        out.setId(base.getId());
                        out.setToken(base.getToken());
                        out.setEmail(base.getEmail());
                        out.setFullName(base.getFullName());
                        out.setRole(base.getRole());
                        return out;
                    });

            CustomerResponseDto resp = authService.loginCustomer(dto);

            assertThat(resp.getId()).isEqualTo(USER_ID);
            assertThat(resp.getToken()).isEqualTo("cust-token");
            assertThat(resp.getEmail()).isEqualTo("cust@ex.com");
            assertThat(resp.getRole()).isEqualTo(customer);
            assertThat(resp.getPhone()).isEqualTo("+7 900 000-00-00");
            assertThat(resp.getOrganization()).isEqualTo("ООО Ромашка");
        }

        @Test
        @DisplayName("ошибка: не-customer роль → InvalidCredentialsException")
        void login_customer_wrong_role() {
            var dto = new LoginDto();
            dto.setEmail("mgr@ex.com");
            dto.setPassword("pass");

            var mgr = user(manager, true, "mgr@ex.com", "Менеджер", "$hash$");
            when(userService.findByEmail("mgr@ex.com")).thenReturn(mgr);
            when(userService.checkPassword(mgr, "pass")).thenReturn(true);

            assertThatThrownBy(() -> authService.loginCustomer(dto))
                    .isInstanceOf(InvalidCredentialsException.class);
            verify(customerService, never()).findCustomerByUserId(any());
        }
    }

    @Nested
    @DisplayName("registerCustomer (клиенты)")
    class RegisterCustomer {

        @BeforeEach
        void mockMapper() {
            when(objectMapper.convertValue(any(AuthResponseDto.class), eq(CustomerResponseDto.class)))
                    .thenAnswer(inv -> {
                        AuthResponseDto base = inv.getArgument(0);
                        CustomerResponseDto out = new CustomerResponseDto();
                        out.setId(base.getId());
                        out.setToken(base.getToken());
                        out.setEmail(base.getEmail());
                        out.setFullName(base.getFullName());
                        out.setRole(base.getRole());
                        return out;
                    });
        }

        @Test
        @DisplayName("успех: создаёт профиль и возвращает CustomerResponseDto")
        void register_customer_success() {
            var req = new ClientRegistrationDto();
            req.setEmail("cust@ex.com");
            req.setFullName("Клиент");
            req.setPhone("+7 900 111-22-33");
            req.setOrganization("ООО Василёк");
            req.setPassword("XX"); // пароль тут не участвует в текущей реализации

            var cust = user(customer, true, "cust@ex.com", "Клиент", "$hash$");
            var profile = new CustomerProfile();
            profile.setUser(cust);
            profile.setPhone("+7 900 111-22-33");
            profile.setOrganization("ООО Василёк");

            when(customerService.registerCustomer(req)).thenReturn(profile);
            when(jwtService.generateToken("cust@ex.com", "customer")).thenReturn("tok");

            CustomerResponseDto resp = authService.registerCustomer(req);

            assertThat(resp.getId()).isEqualTo(USER_ID);
            assertThat(resp.getToken()).isEqualTo("tok");
            assertThat(resp.getEmail()).isEqualTo("cust@ex.com");
            assertThat(resp.getRole()).isEqualTo(customer);
            assertThat(resp.getPhone()).isEqualTo("+7 900 111-22-33");
            assertThat(resp.getOrganization()).isEqualTo("ООО Василёк");

            verify(customerService).registerCustomer(req);
            verify(jwtService).generateToken("cust@ex.com", "customer");
        }
    }
}