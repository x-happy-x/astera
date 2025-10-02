package ru.astera.backend.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import ru.astera.backend.config.TestContainersConfig;
import ru.astera.backend.entity.CustomerProfile;
import ru.astera.backend.entity.User;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestContainersConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=true"
})
class CustomerProfileRepositoryTest {

    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    @Autowired
    private UserRepository userRepository;

    private User customerUser1;
    private User customerUser2;
    private User managerUser;
    private CustomerProfile customerProfile1;
    private CustomerProfile customerProfile2;

    @BeforeEach
    void setUp() {
        // Создаем пользователей-клиентов
        customerUser1 = new User();
        customerUser1.setEmail("customer1@test.com");
        customerUser1.setFullName("Клиент 1");
        customerUser1.setRole(User.Role.customer);
        customerUser1.setIsActive(true);
        customerUser1 = userRepository.save(customerUser1);

        customerUser2 = new User();
        customerUser2.setEmail("customer2@test.com");
        customerUser2.setFullName("Клиент 2");
        customerUser2.setRole(User.Role.customer);
        customerUser2.setIsActive(false);
        customerUser2 = userRepository.save(customerUser2);

        // Создаем пользователя-менеджера (не должен попасть в выборку клиентов)
        managerUser = new User();
        managerUser.setEmail("manager@test.com");
        managerUser.setFullName("Менеджер");
        managerUser.setRole(User.Role.manager);
        managerUser.setIsActive(true);
        managerUser = userRepository.save(managerUser);

        // Создаем профили клиентов
        customerProfile1 = new CustomerProfile();
        customerProfile1.setUser(customerUser1);
        customerProfile1.setPhone("+7 900 111-22-33");
        customerProfile1.setOrganization("ООО Тест 1");
        customerProfile1 = customerProfileRepository.save(customerProfile1);

        customerProfile2 = new CustomerProfile();
        customerProfile2.setUser(customerUser2);
        customerProfile2.setPhone("+7 900 222-33-44");
        customerProfile2.setOrganization("ООО Тест 2");
        customerProfile2 = customerProfileRepository.save(customerProfile2);
    }

    /* =======================
       findCustomerProfileByUserId
       ======================= */

    @Test
    void findCustomerProfileByUserId_Success() {
        // Act
        CustomerProfile result = customerProfileRepository.findCustomerProfileByUserId(customerUser1.getId());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(customerUser1.getId());
        assertThat(result.getUser().getEmail()).isEqualTo("customer1@test.com");
        assertThat(result.getPhone()).isEqualTo("+7 900 111-22-33");
        assertThat(result.getOrganization()).isEqualTo("ООО Тест 1");
    }

    @Test
    void findCustomerProfileByUserId_NotFound() {
        // Arrange
        UUID nonExistentUserId = UUID.randomUUID();

        // Act
        CustomerProfile result = customerProfileRepository.findCustomerProfileByUserId(nonExistentUserId);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void findCustomerProfileByUserId_ManagerUser_NotFound() {
        // Act - ищем профиль по ID менеджера (у менеджеров нет CustomerProfile)
        CustomerProfile result = customerProfileRepository.findCustomerProfileByUserId(managerUser.getId());

        // Assert
        assertThat(result).isNull();
    }

    /* =======================
       findAllCustomersWithUsers
       ======================= */

    @Test
    void findAllCustomersWithUsers_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<CustomerProfile> result = customerProfileRepository.findAllCustomersWithUsers(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(10);

        // Проверяем, что загружены пользователи (JOIN FETCH)
        result.getContent().forEach(profile -> {
            assertThat(profile.getUser()).isNotNull();
            assertThat(profile.getUser().getRole()).isEqualTo(User.Role.customer);
        });

        // Проверяем конкретные данные
        assertThat(result.getContent())
                .extracting(profile -> profile.getUser().getEmail())
                .containsExactlyInAnyOrder("customer1@test.com", "customer2@test.com");
    }

    @Test
    void findAllCustomersWithUsers_WithPagination() {
        // Arrange - запрашиваем только 1 элемент на страницу
        Pageable pageable = PageRequest.of(0, 1);

        // Act
        Page<CustomerProfile> result = customerProfileRepository.findAllCustomersWithUsers(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(1);

        // Проверяем вторую страницу
        Pageable secondPageable = PageRequest.of(1, 1);
        Page<CustomerProfile> secondPage = customerProfileRepository.findAllCustomersWithUsers(secondPageable);

        assertThat(secondPage.getContent()).hasSize(1);
        assertThat(secondPage.getNumber()).isEqualTo(1);
        
        // Проверяем, что на разных страницах разные клиенты
        String firstPageEmail = result.getContent().get(0).getUser().getEmail();
        String secondPageEmail = secondPage.getContent().get(0).getUser().getEmail();
        assertThat(firstPageEmail).isNotEqualTo(secondPageEmail);
    }

    @Test
    void findAllCustomersWithUsers_EmptyResult() {
        // Arrange - удаляем всех клиентов
        customerProfileRepository.deleteAll();
        userRepository.deleteAll();

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<CustomerProfile> result = customerProfileRepository.findAllCustomersWithUsers(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getTotalPages()).isEqualTo(0);
    }

    @Test
    void findAllCustomersWithUsers_OnlyCustomerRoleReturned() {
        // Arrange - создаем дополнительного пользователя-админа
        User adminUser = new User();
        adminUser.setEmail("admin@test.com");
        adminUser.setFullName("Администратор");
        adminUser.setRole(User.Role.admin);
        adminUser.setIsActive(true);
        userRepository.save(adminUser);

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<CustomerProfile> result = customerProfileRepository.findAllCustomersWithUsers(pageable);

        // Assert
        assertThat(result.getContent()).hasSize(2); // только клиенты
        result.getContent().forEach(profile -> {
            assertThat(profile.getUser().getRole()).isEqualTo(User.Role.customer);
        });
    }

    /* =======================
       Базовые операции JpaRepository
       ======================= */

    @Test
    void findById_Success() {
        // Act
        var result = customerProfileRepository.findById(customerProfile1.getUserId());

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo(customerUser1.getId());
        assertThat(result.get().getPhone()).isEqualTo("+7 900 111-22-33");
    }

    @Test
    void findById_NotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act
        var result = customerProfileRepository.findById(nonExistentId);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void deleteById_Success() {
        // Arrange
        UUID profileId = customerProfile1.getUserId();
        assertThat(customerProfileRepository.findById(profileId)).isPresent();

        // Act
        customerProfileRepository.deleteById(profileId);

        // Assert
        assertThat(customerProfileRepository.findById(profileId)).isEmpty();
    }

    @Test
    void count_Success() {
        // Act
        long count = customerProfileRepository.count();

        // Assert
        assertThat(count).isEqualTo(2);
    }
}