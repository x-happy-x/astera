package ru.astera.backend.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import ru.astera.backend.dto.selection.ConfigurationComponentDto;
import ru.astera.backend.entity.ConfigCandidate;
import ru.astera.backend.entity.ConfigComponent;
import ru.astera.backend.entity.ConfigComponentId;
import ru.astera.backend.entity.Equipment;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring",
        uses = {},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConfigComponentMapper {

    @Mappings({
            @Mapping(target = "equipmentId", source = "equipment.id"),
            @Mapping(target = "category", source = "category"),
            @Mapping(target = "brand", source = "equipment.brand"),
            @Mapping(target = "model", source = "equipment.model"),
            @Mapping(target = "dnSize", source = "equipment.dnSize"),
            @Mapping(target = "connectionKey", source = "equipment.connectionKey"),
            @Mapping(target = "deliveryDays", source = "equipment.deliveryDays"),
            @Mapping(target = "qty", source = "qty"),
            @Mapping(target = "unitPrice", source = "unitPrice"),
            @Mapping(target = "subtotal", source = "subtotal")
    })
    ConfigurationComponentDto toDto(ConfigComponent entity);

    List<ConfigurationComponentDto> toDtoList(List<ConfigComponent> entities);

    default ConfigComponent toEntity(ConfigurationComponentDto dto, UUID candidateId) {
        if (dto == null) {
            return null;
        }

        ConfigComponent entity = new ConfigComponent();

        // EmbeddedId
        ConfigComponentId id = new ConfigComponentId(candidateId, dto.equipmentId());
        entity.setId(id);

        // Ссылки по id (без загрузки)
        ConfigCandidate candRef = new ConfigCandidate();
        candRef.setId(candidateId);
        entity.setCandidate(candRef);

        Equipment eqRef = new Equipment();
        eqRef.setId(dto.equipmentId());
        entity.setEquipment(eqRef);

        // Поля компонента
        entity.setCategory(dto.category());
        entity.setQty(dto.qty() != null ? dto.qty() : BigDecimal.ONE);
        entity.setUnitPrice(dto.unitPrice());
        entity.setSubtotal(dto.subtotal());

        return entity;
    }

    default ConfigComponent toEntity(ConfigurationComponentDto dto) {
        if (dto == null) {
            return null;
        }

        ConfigComponent entity = new ConfigComponent();

        // Пока знаем только equipment_id
        ConfigComponentId id = new ConfigComponentId(null, dto.equipmentId());
        entity.setId(id);

        Equipment eqRef = new Equipment();
        eqRef.setId(dto.equipmentId());
        entity.setEquipment(eqRef);

        entity.setCategory(dto.category());
        entity.setQty(dto.qty() != null ? dto.qty() : BigDecimal.ONE);
        entity.setUnitPrice(dto.unitPrice());
        entity.setSubtotal(dto.subtotal());

        return entity;
    }
}
