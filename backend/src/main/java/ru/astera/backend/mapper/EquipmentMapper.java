package ru.astera.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import ru.astera.backend.dto.selection.ConfigurationComponentDto;
import ru.astera.backend.entity.Equipment;

import java.math.BigDecimal;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EquipmentMapper {

    @Mappings({
            @Mapping(target = "equipmentId", source = "id"),
            @Mapping(target = "category", source = "category"),
            @Mapping(target = "brand", source = "brand"),
            @Mapping(target = "model", source = "model"),
            @Mapping(target = "dnSize", source = "dnSize"),
            @Mapping(target = "connectionKey", source = "connectionKey"),
            @Mapping(target = "deliveryDays", source = "deliveryDays"),
            @Mapping(target = "qty", expression = "java(java.math.BigDecimal.ONE)"),
            @Mapping(target = "unitPrice", source = "price"),
            @Mapping(target = "subtotal", source = "price")
    })
    ConfigurationComponentDto toComponentDto(Equipment equipment);

    default ConfigurationComponentDto toComponentDto(Equipment equipment, BigDecimal qty) {
        ConfigurationComponentDto base = toComponentDto(equipment);
        return new ConfigurationComponentDto(
                base.equipmentId(),
                base.category(),
                base.brand(),
                base.model(),
                base.dnSize(),
                base.connectionKey(),
                base.deliveryDays(),
                qty,
                base.unitPrice(),
                base.unitPrice().multiply(qty)
        );
    }
}
