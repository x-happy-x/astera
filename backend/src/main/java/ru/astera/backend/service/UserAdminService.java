package ru.astera.backend.service;

import ru.astera.backend.dto.admin.UserCreateDto;
import ru.astera.backend.dto.admin.UserDto;
import ru.astera.backend.dto.admin.UserPageDto;
import ru.astera.backend.dto.admin.UserUpdateDto;
import ru.astera.backend.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserAdminService {
    UserPageDto getUsersWithPagination(int page, int size, List<User.Role> roles);

    UserDto getUserById(UUID id);

    UserDto createUser(UserCreateDto dto);

    UserDto updateUser(UUID id, UserUpdateDto dto);

    void deleteUser(UUID id);
}
