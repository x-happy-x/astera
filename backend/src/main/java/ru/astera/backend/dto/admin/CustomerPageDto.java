package ru.astera.backend.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CustomerPageDto {
    @JsonProperty("customers")
    private List<CustomerDto> customers;

    @JsonProperty("totalCustomers")
    private Long totalCustomers;

    @JsonProperty("currentPage")
    private Integer currentPage;

    @JsonProperty("totalPages")
    private Integer totalPages;

    @JsonProperty("pageSize")
    private Integer pageSize;
}