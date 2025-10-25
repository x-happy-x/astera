package ru.astera.backend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.astera.backend.dto.admin.UserCreateDto;
import ru.astera.backend.dto.admin.UserDto;
import ru.astera.backend.dto.admin.UserPageDto;
import ru.astera.backend.dto.admin.UserUpdateDto;
import ru.astera.backend.entity.User;
import ru.astera.backend.exception.UserAlreadyExistsException;
import ru.astera.backend.exception.UserNotFoundException;
import ru.astera.backend.repository.UserRepository;
import ru.astera.backend.service.UserAdminService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAdminServiceImpl implements UserAdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserPageDto getUsersWithPagination(int page, int size, List<User.Role> roles) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<User> userPage;
        if (roles != null && !roles.isEmpty()) {
            userPage = userRepository.findByRoleIn(roles, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        List<UserDto> userList = userPage.getContent().stream()
                .map(this::convertToDto)
                .toList();

        UserPageDto result = new UserPageDto();
        result.setUsers(userList);
        result.setTotalUsers(userPage.getTotalElements());
        result.setCurrentPage(page);
        result.setTotalPages(userPage.getTotalPages());
        result.setPageSize(size);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return convertToDto(user);
    }

    @Override
    @Transactional
    public UserDto createUser(UserCreateDto dto) {
        if (userRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + dto.getEmail() + " already exists");
        }

        User user = convertFromCreateDto(dto);
        User savedUser = userRepository.save(user);
        log.info("Created user: {} with role {} and id: {}", savedUser.getEmail(), savedUser.getRole(), savedUser.getId());
        return convertToDto(savedUser);
    }

    @Override
    @Transactional
    public UserDto updateUser(UUID id, UserUpdateDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        // Check if email is being changed and if it's already taken by another user
        if (!user.getEmail().equalsIgnoreCase(dto.getEmail()) &&
                userRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + dto.getEmail() + " already exists");
        }

        updateUserFromDto(user, dto);
        User updatedUser = userRepository.save(user);
        log.info("Updated user: {} with role {} and id: {}", updatedUser.getEmail(), updatedUser.getRole(), updatedUser.getId());
        return convertToDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        userRepository.delete(user);
        log.info("Deleted user: {} with id: {}", user.getEmail(), user.getId());
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setRole(user.getRole());
        dto.setIsActive(user.getIsActive());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    private User convertFromCreateDto(UserCreateDto dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setFullName(dto.getFullName());
        user.setRole(dto.getRole());
        user.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }

        return user;
    }

    private void updateUserFromDto(User user, UserUpdateDto dto) {
        user.setEmail(dto.getEmail());
        user.setFullName(dto.getFullName());
        user.setRole(dto.getRole());
        user.setIsActive(dto.getIsActive());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }
    }
}
