package ru.astera.backend.mapper;

import org.mapstruct.*;
import ru.astera.backend.dto.selection.HeatingRequestCreateDto;
import ru.astera.backend.dto.selection.HeatingRequestDto;
import ru.astera.backend.dto.selection.HeatingRequestUpdateDto;
import ru.astera.backend.entity.CustomerProfile;
import ru.astera.backend.entity.HeatingRequest;

import java.util.UUID;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HeatingRequestMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "customerProfile", expression = "java(refCustomerProfile(dto.customerId()))"),
            @Mapping(target = "status", constant = "CREATED"),
            @Mapping(target = "createdAt", ignore = true) // выставит БД/сервис
    })
    HeatingRequest toEntity(HeatingRequestCreateDto dto);

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "customerId", source = "customerProfile.userId"),
            @Mapping(target = "powerKw", source = "powerKw"),
            @Mapping(target = "tIn", source = "TIn"),
            @Mapping(target = "tOut", source = "TOut"),
            @Mapping(target = "fuelType", source = "fuelType"),
            @Mapping(target = "notes", source = "notes")
    })
    HeatingRequestDto toDto(HeatingRequest entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mappings({
            @Mapping(target = "powerKw", source = "powerKw"),
            @Mapping(target = "TIn", source = "tIn"),
            @Mapping(target = "TOut", source = "tOut"),
            @Mapping(target = "fuelType", source = "fuelType"),
            @Mapping(target = "notes", source = "notes")
    })
    void update(@MappingTarget HeatingRequest entity, HeatingRequestUpdateDto dto);

    default CustomerProfile refCustomerProfile(UUID id) {
        if (id == null) {
            return null;
        }
        CustomerProfile cp = new CustomerProfile();
        cp.setUserId(id);
        return cp;
    }
}
