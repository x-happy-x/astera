package ru.astera.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import ru.astera.backend.dto.admin.CustomerCreateDto;
import ru.astera.backend.dto.admin.CustomerDto;
import ru.astera.backend.dto.admin.CustomerPageDto;
import ru.astera.backend.dto.admin.CustomerUpdateDto;
import ru.astera.backend.dto.registration.CustomerRegistrationDto;
import ru.astera.backend.entity.CustomerProfile;
import ru.astera.backend.entity.User;
import ru.astera.backend.exception.UserAlreadyExistsException;
import ru.astera.backend.repository.CustomerProfileRepository;
import ru.astera.backend.repository.UserRepository;
import ru.astera.backend.service.impl.CustomerServiceImpl;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private CustomerProfileRepository profileRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private User testUser;
    private CustomerProfile testProfile;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();

        testUser = new User();
        testUser.setId(testUserId);
        testUser.setEmail("test@example.com");
        testUser.setFullName("Тестовый Пользователь");
        testUser.setRole(User.Role.customer);
        testUser.setIsActive(true);
        testUser.setCreatedAt(OffsetDateTime.now());

        testProfile = new CustomerProfile();
        testProfile.setUserId(testUserId);
        testProfile.setUser(testUser);
        testProfile.setPhone("+7 900 123-45-67");
        testProfile.setOrganization("ООО Тест");
        testProfile.setCreatedAt(OffsetDateTime.now());
    }

    /* =======================
       getCustomersWithPagination
       ======================= */

    @Test
    void getCustomersWithPagination_Success() {
        // Arrange
        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setEmail("test2@example.com");
        user2.setFullName("Тестовый Пользователь 2");
        user2.setRole(User.Role.customer);
        user2.setIsActive(false);

        CustomerProfile profile2 = new CustomerProfile();
        profile2.setUserId(user2.getId());
        profile2.setUser(user2);
        profile2.setPhone("+7 900 987-65-43");
        profile2.setOrganization("ООО Тест 2");

        Page<CustomerProfile> mockPage = new PageImpl<>(
                Arrays.asList(testProfile, profile2),
                PageRequest.of(0, 20),
                2L
        );

        when(profileRepository.findAllCustomersWithUsers(any(Pageable.class)))
                .thenReturn(mockPage);

        // Act
        CustomerPageDto result = customerService.getCustomersWithPagination(0, 20);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getCustomers()).hasSize(2);
        assertThat(result.getTotalCustomers()).isEqualTo(2L);
        assertThat(result.getCurrentPage()).isEqualTo(0);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getPageSize()).isEqualTo(20);

        CustomerDto firstCustomer = result.getCustomers().get(0);
        assertThat(firstCustomer.getId()).isEqualTo(testUserId);
        assertThat(firstCustomer.getEmail()).isEqualTo("test@example.com");
        assertThat(firstCustomer.getFullName()).isEqualTo("Тестовый Пользователь");
        assertThat(firstCustomer.getPhone()).isEqualTo("+7 900 123-45-67");
        assertThat(firstCustomer.getOrganization()).isEqualTo("ООО Тест");
        assertThat(firstCustomer.getIsActive()).isTrue();

        verify(profileRepository).findAllCustomersWithUsers(any(Pageable.class));
    }

    /* =======================
       getCustomerById
       ======================= */

    @Test
    void getCustomerById_Success() {
        // Arrange
        when(profileRepository.findById(testUserId)).thenReturn(Optional.of(testProfile));

        // Act
        CustomerDto result = customerService.getCustomerById(testUserId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testUserId);
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getFullName()).isEqualTo("Тестовый Пользователь");
        assertThat(result.getPhone()).isEqualTo("+7 900 123-45-67");
        assertThat(result.getOrganization()).isEqualTo("ООО Тест");
        assertThat(result.getIsActive()).isTrue();

        verify(profileRepository).findById(testUserId);
    }

    @Test
    void getCustomerById_NotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(profileRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> customerService.getCustomerById(nonExistentId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Клиент не найден");

        verify(profileRepository).findById(nonExistentId);
    }

    /* =======================
       createCustomer
       ======================= */

    @Test
    void createCustomer_Success_WithPassword() {
        // Arrange
        CustomerCreateDto dto = new CustomerCreateDto();
        dto.setEmail("new@example.com");
        dto.setFullName("Новый Клиент");
        dto.setPhone("+7 900 111-22-33");
        dto.setOrganization("ООО Новая");
        dto.setPassword("password123");

        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setEmail(dto.getEmail());
        savedUser.setFullName(dto.getFullName());
        savedUser.setRole(User.Role.customer);
        savedUser.setIsActive(true);

        CustomerProfile savedProfile = new CustomerProfile();
        savedProfile.setUserId(savedUser.getId());
        savedProfile.setUser(savedUser);
        savedProfile.setPhone(dto.getPhone());
        savedProfile.setOrganization(dto.getOrganization());

        when(userService.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(profileRepository.save(any(CustomerProfile.class))).thenReturn(savedProfile);

        // Act
        CustomerDto result = customerService.createCustomer(dto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("new@example.com");
        assertThat(result.getFullName()).isEqualTo("Новый Клиент");
        assertThat(result.getPhone()).isEqualTo("+7 900 111-22-33");
        assertThat(result.getOrganization()).isEqualTo("ООО Новая");
        assertThat(result.getIsActive()).isTrue();

        verify(userService).existsByEmail(dto.getEmail());
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(profileRepository).save(any(CustomerProfile.class));
    }

    @Test
    void createCustomer_Success_WithoutPassword() {
        // Arrange
        CustomerCreateDto dto = new CustomerCreateDto();
        dto.setEmail("nopwd@example.com");
        dto.setFullName("Клиент без пароля");
        dto.setPhone("+7 900 222-33-44");
        dto.setOrganization("ООО Без пароля");
        dto.setPassword(null);

        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setEmail(dto.getEmail());
        savedUser.setFullName(dto.getFullName());
        savedUser.setRole(User.Role.customer);
        savedUser.setIsActive(true);

        CustomerProfile savedProfile = new CustomerProfile();
        savedProfile.setUserId(savedUser.getId());
        savedProfile.setUser(savedUser);
        savedProfile.setPhone(dto.getPhone());
        savedProfile.setOrganization(dto.getOrganization());

        when(userService.existsByEmail(dto.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(profileRepository.save(any(CustomerProfile.class))).thenReturn(savedProfile);

        // Act
        CustomerDto result = customerService.createCustomer(dto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("nopwd@example.com");

        verify(userService).existsByEmail(dto.getEmail());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository).save(any(User.class));
        verify(profileRepository).save(any(CustomerProfile.class));
    }

    @Test
    void createCustomer_EmailAlreadyExists() {
        // Arrange
        CustomerCreateDto dto = new CustomerCreateDto();
        dto.setEmail("existing@example.com");
        dto.setFullName("Дублирующийся Email");
        dto.setPhone("+7 900 333-44-55");
        dto.setOrganization("ООО Дубликат");

        CustomerRegistrationDto dto2 = new CustomerRegistrationDto();
        dto2.setEmail("existing@example.com");
        dto2.setFullName("Дублирующийся Email");
        dto2.setPhone("+7 900 333-44-55");
        dto2.setOrganization("ООО Дубликат");

        when(userService.existsByEmail(dto.getEmail())).thenReturn(true);
        when(objectMapper.convertValue(any(), eq(CustomerRegistrationDto.class))).thenReturn(dto2);

        // Act & Assert
        assertThatThrownBy(() -> customerService.createCustomer(dto))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("Пользователь с таким email уже существует");

        verify(userService).existsByEmail(dto.getEmail());
        verify(userRepository, never()).save(any(User.class));
        verify(profileRepository, never()).save(any(CustomerProfile.class));
    }

    /* =======================
       updateCustomer
       ======================= */

    @Test
    void updateCustomer_Success() {
        // Arrange
        CustomerUpdateDto dto = new CustomerUpdateDto();
        dto.setEmail("updated@example.com");
        dto.setFullName("Обновленный Клиент");
        dto.setPhone("+7 900 444-55-66");
        dto.setOrganization("ООО Обновленная");
        dto.setIsActive(false);

        when(profileRepository.findById(testUserId)).thenReturn(Optional.of(testProfile));
        when(userService.existsByEmail(dto.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(profileRepository.save(any(CustomerProfile.class))).thenReturn(testProfile);

        // Act
        CustomerDto result = customerService.updateCustomer(testUserId, dto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(testUser.getEmail()).isEqualTo("updated@example.com");
        assertThat(testUser.getFullName()).isEqualTo("Обновленный Клиент");
        assertThat(testUser.getIsActive()).isFalse();
        assertThat(testProfile.getPhone()).isEqualTo("+7 900 444-55-66");
        assertThat(testProfile.getOrganization()).isEqualTo("ООО Обновленная");

        verify(profileRepository).findById(testUserId);
        verify(userService).existsByEmail(dto.getEmail());
        verify(userRepository).save(testUser);
        verify(profileRepository).save(testProfile);
    }

    @Test
    void updateCustomer_SameEmail_Success() {
        // Arrange
        CustomerUpdateDto dto = new CustomerUpdateDto();
        dto.setEmail("test@example.com"); // тот же email
        dto.setFullName("Тот же Email");
        dto.setPhone("+7 900 555-66-77");
        dto.setOrganization("ООО Тот же");
        dto.setIsActive(true);

        when(profileRepository.findById(testUserId)).thenReturn(Optional.of(testProfile));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(profileRepository.save(any(CustomerProfile.class))).thenReturn(testProfile);

        // Act
        CustomerDto result = customerService.updateCustomer(testUserId, dto);

        // Assert
        assertThat(result).isNotNull();
        verify(profileRepository).findById(testUserId);
        verify(userService, never()).existsByEmail(anyString()); // не проверяем существование того же email
        verify(userRepository).save(testUser);
        verify(profileRepository).save(testProfile);
    }

    @Test
    void updateCustomer_EmailAlreadyExists() {
        // Arrange
        CustomerUpdateDto dto = new CustomerUpdateDto();
        dto.setEmail("other@example.com");
        dto.setFullName("Другой Email");
        dto.setPhone("+7 900 666-77-88");
        dto.setOrganization("ООО Другая");
        dto.setIsActive(true);

        when(profileRepository.findById(testUserId)).thenReturn(Optional.of(testProfile));
        when(userService.existsByEmail("other@example.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> customerService.updateCustomer(testUserId, dto))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("Пользователь с таким email уже существует");

        verify(profileRepository).findById(testUserId);
        verify(userService).existsByEmail("other@example.com");
        verify(userRepository, never()).save(any(User.class));
        verify(profileRepository, never()).save(any(CustomerProfile.class));
    }

    @Test
    void updateCustomer_NotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        CustomerUpdateDto dto = new CustomerUpdateDto();
        dto.setEmail("test@example.com");
        dto.setFullName("Test");
        dto.setPhone("+7 900 000-00-00");
        dto.setOrganization("Test");
        dto.setIsActive(true);

        when(profileRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> customerService.updateCustomer(nonExistentId, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Клиент не найден");

        verify(profileRepository).findById(nonExistentId);
        verify(userService, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(profileRepository, never()).save(any(CustomerProfile.class));
    }

    /* =======================
       deleteCustomer
       ======================= */

    @Test
    void deleteCustomer_Success() {
        // Arrange
        when(profileRepository.findById(testUserId)).thenReturn(Optional.of(testProfile));

        // Act
        customerService.deleteCustomer(testUserId);

        // Assert
        verify(profileRepository).findById(testUserId);
        verify(profileRepository).delete(testProfile);
        verify(userRepository).delete(testUser);
    }

    @Test
    void deleteCustomer_NotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(profileRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> customerService.deleteCustomer(nonExistentId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Клиент не найден");

        verify(profileRepository).findById(nonExistentId);
        verify(profileRepository, never()).delete(any(CustomerProfile.class));
        verify(userRepository, never()).delete(any(User.class));
    }
}