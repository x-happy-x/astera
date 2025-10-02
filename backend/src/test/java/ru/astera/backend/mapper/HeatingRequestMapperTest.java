package ru.astera.backend.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.astera.backend.dto.selection.HeatingRequestCreateDto;
import ru.astera.backend.dto.selection.HeatingRequestUpdateDto;
import ru.astera.backend.dto.selection.HeatingRequestDto;
import ru.astera.backend.entity.CustomerProfile;
import ru.astera.backend.entity.FuelType;
import ru.astera.backend.entity.HeatingRequest;
import ru.astera.backend.entity.HeatingRequestStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class HeatingRequestMapperTest {

    private HeatingRequestMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(HeatingRequestMapper.class);
    }

    @Test
    void shouldMapCreateDtoToEntity() {
        // given
        UUID customerId = UUID.randomUUID();
        HeatingRequestCreateDto dto = HeatingRequestCreateDto.builder()
                .customerId(customerId)
                .powerKw(new BigDecimal("50.5"))
                .tIn(new BigDecimal("20.0"))
                .tOut(new BigDecimal("70.0"))
                .fuelType(FuelType.gas)
                .notes("Test notes")
                .build();

        // when
        HeatingRequest entity = mapper.toEntity(dto);

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNull(); // должен игнорироваться
        assertThat(entity.getCustomerProfile()).isNotNull();
        assertThat(entity.getCustomerProfile().getUserId()).isEqualTo(customerId);
        assertThat(entity.getPowerKw()).isEqualByComparingTo(new BigDecimal("50.5"));
        assertThat(entity.getTIn()).isEqualByComparingTo(new BigDecimal("20.0"));
        assertThat(entity.getTOut()).isEqualByComparingTo(new BigDecimal("70.0"));
        assertThat(entity.getFuelType()).isEqualTo(FuelType.gas);
        assertThat(entity.getNotes()).isEqualTo("Test notes");
        assertThat(entity.getStatus()).isEqualTo(HeatingRequestStatus.CREATED);
        assertThat(entity.getCreatedAt()).isNull(); // должен игнорироваться
    }

    @Test
    void shouldMapEntityToDto() {
        // given
        UUID entityId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        
        CustomerProfile customerProfile = new CustomerProfile();
        customerProfile.setUserId(customerId);

        HeatingRequest entity = HeatingRequest.builder()
                .id(entityId)
                .customerProfile(customerProfile)
                .powerKw(new BigDecimal("75.25"))
                .tIn(new BigDecimal("15.5"))
                .tOut(new BigDecimal("80.0"))
                .fuelType(FuelType.diesel)
                .notes("Entity notes")
                .status(HeatingRequestStatus.CREATED)
                .createdAt(OffsetDateTime.now())
                .build();

        // when
        HeatingRequestDto dto = mapper.toDto(entity);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(entityId);
        assertThat(dto.customerId()).isEqualTo(customerId);
        assertThat(dto.powerKw()).isEqualByComparingTo(new BigDecimal("75.25"));
        assertThat(dto.tIn()).isEqualByComparingTo(new BigDecimal("15.5"));
        assertThat(dto.tOut()).isEqualByComparingTo(new BigDecimal("80.0"));
        assertThat(dto.fuelType()).isEqualTo(FuelType.diesel);
        assertThat(dto.notes()).isEqualTo("Entity notes");
    }

    @Test
    void shouldUpdateEntityFromUpdateDto() {
        // given
        UUID customerId = UUID.randomUUID();
        CustomerProfile customerProfile = new CustomerProfile();
        customerProfile.setUserId(customerId);

        HeatingRequest entity = HeatingRequest.builder()
                .id(UUID.randomUUID())
                .customerProfile(customerProfile)
                .powerKw(new BigDecimal("100.0"))
                .tIn(new BigDecimal("25.0"))
                .tOut(new BigDecimal("60.0"))
                .fuelType(FuelType.gas)
                .notes("Original notes")
                .status(HeatingRequestStatus.CREATED)
                .createdAt(OffsetDateTime.now())
                .build();

        HeatingRequestUpdateDto updateDto = HeatingRequestUpdateDto.builder()
                .powerKw(new BigDecimal("120.0"))
                .tOut(new BigDecimal("65.0"))
                .fuelType(FuelType.diesel)
                .notes("Updated notes")
                .build();

        // when
        mapper.update(entity, updateDto);

        // then
        assertThat(entity.getPowerKw()).isEqualByComparingTo(new BigDecimal("120.0"));
        assertThat(entity.getTIn()).isEqualByComparingTo(new BigDecimal("25.0")); // не изменился
        assertThat(entity.getTOut()).isEqualByComparingTo(new BigDecimal("65.0"));
        assertThat(entity.getFuelType()).isEqualTo(FuelType.diesel);
        assertThat(entity.getNotes()).isEqualTo("Updated notes");
        // Другие поля должны остаться неизменными
        assertThat(entity.getId()).isNotNull();
        assertThat(entity.getCustomerProfile()).isNotNull();
        assertThat(entity.getStatus()).isEqualTo(HeatingRequestStatus.CREATED);
        assertThat(entity.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldHandleNullValuesInUpdate() {
        // given
        UUID customerId = UUID.randomUUID();
        CustomerProfile customerProfile = new CustomerProfile();
        customerProfile.setUserId(customerId);

        HeatingRequest entity = HeatingRequest.builder()
                .id(UUID.randomUUID())
                .customerProfile(customerProfile)
                .powerKw(new BigDecimal("100.0"))
                .tIn(new BigDecimal("25.0"))
                .tOut(new BigDecimal("60.0"))
                .fuelType(FuelType.gas)
                .notes("Original notes")
                .status(HeatingRequestStatus.CREATED)
                .createdAt(OffsetDateTime.now())
                .build();

        HeatingRequestUpdateDto updateDto = HeatingRequestUpdateDto.builder()
                .powerKw(new BigDecimal("150.0"))
                .tIn(null) // должен игнорироваться
                .tOut(null) // должен игнорироваться
                .fuelType(null) // должен игнорироваться
                .notes(null) // должен игнорироваться
                .build();

        // when
        mapper.update(entity, updateDto);

        // then
        assertThat(entity.getPowerKw()).isEqualByComparingTo(new BigDecimal("150.0"));
        assertThat(entity.getTIn()).isEqualByComparingTo(new BigDecimal("25.0")); // не изменился
        assertThat(entity.getTOut()).isEqualByComparingTo(new BigDecimal("60.0")); // не изменился
        assertThat(entity.getFuelType()).isEqualTo(FuelType.gas); // не изменился
        assertThat(entity.getNotes()).isEqualTo("Original notes"); // не изменился
    }

    @Test
    void shouldHandleNullCustomerIdInCreateDto() {
        // given
        HeatingRequestCreateDto dto = HeatingRequestCreateDto.builder()
                .customerId(null)
                .powerKw(new BigDecimal("50.0"))
                .tIn(new BigDecimal("20.0"))
                .tOut(new BigDecimal("70.0"))
                .fuelType(FuelType.gas)
                .notes("Test notes")
                .build();

        // when
        HeatingRequest entity = mapper.toEntity(dto);

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getCustomerProfile()).isNull();
        assertThat(entity.getPowerKw()).isEqualByComparingTo(new BigDecimal("50.0"));
        assertThat(entity.getFuelType()).isEqualTo(FuelType.gas);
        assertThat(entity.getStatus()).isEqualTo(HeatingRequestStatus.CREATED);
    }

    @Test
    void shouldHandleNullCustomerProfileInEntity() {
        // given
        HeatingRequest entity = HeatingRequest.builder()
                .id(UUID.randomUUID())
                .customerProfile(null)
                .powerKw(new BigDecimal("75.0"))
                .tIn(new BigDecimal("15.0"))
                .tOut(new BigDecimal("80.0"))
                .fuelType(FuelType.diesel)
                .notes("Entity notes")
                .status(HeatingRequestStatus.CREATED)
                .createdAt(OffsetDateTime.now())
                .build();

        // when
        HeatingRequestDto dto = mapper.toDto(entity);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.customerId()).isNull();
        assertThat(dto.powerKw()).isEqualByComparingTo(new BigDecimal("75.0"));
        assertThat(dto.fuelType()).isEqualTo(FuelType.diesel);
    }
}