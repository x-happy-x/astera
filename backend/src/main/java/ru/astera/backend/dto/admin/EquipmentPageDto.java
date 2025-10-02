package ru.astera.backend.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class EquipmentPageDto {
    @JsonProperty("equipment")
    private List<EquipmentDto> equipment;

    @JsonProperty("totalEquipment")
    private Long totalEquipment;

    @JsonProperty("currentPage")
    private Integer currentPage;

    @JsonProperty("totalPages")
    private Integer totalPages;

    @JsonProperty("pageSize")
    private Integer pageSize;
}