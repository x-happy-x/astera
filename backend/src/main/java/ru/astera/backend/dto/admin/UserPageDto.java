package ru.astera.backend.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class UserPageDto {
    @JsonProperty("users")
    private List<UserDto> users;

    @JsonProperty("totalUsers")
    private Long totalUsers;

    @JsonProperty("currentPage")
    private Integer currentPage;

    @JsonProperty("totalPages")
    private Integer totalPages;

    @JsonProperty("pageSize")
    private Integer pageSize;
}
