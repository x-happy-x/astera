package ru.astera.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import ru.astera.backend.dto.selection.ConfigurationCandidateDto;
import ru.astera.backend.entity.ConfigCandidate;
import ru.astera.backend.entity.HeatingRequest;

import java.util.Objects;
import java.util.UUID;

@Mapper(componentModel = "spring",
        uses = {ConfigComponentMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConfigCandidateMapper {

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "requestId", source = "request.id"),
            @Mapping(target = "totalPrice", source = "totalPrice"),
            @Mapping(target = "currency", source = "currency"),
            @Mapping(target = "components", source = "components"),
            // calc fields:
            @Mapping(target = "maxDeliveryDays", expression = "java(calcMaxDeliveryDays(entity))"),
            @Mapping(target = "connectionKey", expression = "java(deriveConnectionKey(entity))"),
            @Mapping(target = "dnSize", expression = "java(deriveDnSize(entity))")
    })
    ConfigurationCandidateDto toDto(ConfigCandidate entity);

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "requestId", source = "request.id"),
            @Mapping(target = "totalPrice", source = "totalPrice"),
            @Mapping(target = "currency", source = "currency"),
            @Mapping(target = "components", ignore = true),
            @Mapping(target = "maxDeliveryDays", expression = "java(calcMaxDeliveryDays(entity))"),
            @Mapping(target = "connectionKey", expression = "java(deriveConnectionKey(entity))"),
            @Mapping(target = "dnSize", expression = "java(deriveDnSize(entity))")
    })
    ConfigurationCandidateDto toDtoWithoutComponents(ConfigCandidate entity);

    @Mappings({
            @Mapping(target = "request", expression = "java(refRequest(dto.requestId()))"),
            @Mapping(target = "components", ignore = true), // компоненты сохраняем отдельно
            @Mapping(target = "createdAt", ignore = true)   // выставит БД/сервис
    })
    ConfigCandidate toEntity(ConfigurationCandidateDto dto);

    default HeatingRequest refRequest(UUID id) {
        if (id == null) {
            return null;
        }
        HeatingRequest r = new HeatingRequest();
        r.setId(id);
        return r;
    }

    default Integer calcMaxDeliveryDays(ConfigCandidate entity) {
        if (entity == null || entity.getComponents() == null) {
            return null;
        }
        return entity.getComponents().stream()
                .map(c -> c.getEquipment() != null ? c.getEquipment().getDeliveryDays() : null)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(null);
    }

    /**
     * Пытаемся вывести connectionKey исходя из пары котёл/горелка внутри набора (если есть).
     * Если нет — вернём null. Это поле вторично и не критично для сохранения.
     */
    default String deriveConnectionKey(ConfigCandidate entity) {
        if (entity == null || entity.getComponents() == null) {
            return null;
        }
        return entity.getComponents().stream()
                .map(c -> c.getEquipment() != null ? c.getEquipment().getConnectionKey() : null)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    /**
     * Аналогично DN берём у котла/арматуры, если он присутствует.
     */
    default Integer deriveDnSize(ConfigCandidate entity) {
        if (entity == null || entity.getComponents() == null) {
            return null;
        }
        return entity.getComponents().stream()
                .map(c -> c.getEquipment() != null ? c.getEquipment().getDnSize() : null)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}
