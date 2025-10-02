package ru.astera.backend.service.impl;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.astera.backend.dto.selection.ConfigurationCandidateDto;
import ru.astera.backend.dto.selection.ConfigurationComponentDto;
import ru.astera.backend.dto.selection.HeatingRequestDto;
import ru.astera.backend.entity.Equipment;
import ru.astera.backend.mapper.EquipmentMapper;
import ru.astera.backend.repository.EquipmentRepository;
import ru.astera.backend.service.ConfigurationSelectionService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConfigurationSelectionServiceImpl implements ConfigurationSelectionService {

    private final EquipmentRepository equipmentRepo;
    private final EquipmentMapper equipmentMapper;

    public ConfigurationSelectionServiceImpl(EquipmentRepository equipmentRepo,
                                             EquipmentMapper equipmentMapper) {
        this.equipmentRepo = equipmentRepo;
        this.equipmentMapper = equipmentMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConfigurationCandidateDto> selectTopConfigurations(HeatingRequestDto req,
                                                                   int topN,
                                                                   boolean includeAutomation) {
        validate(req);

        BigDecimal deltaT = req.tIn().subtract(req.tOut());
        BigDecimal flow = new BigDecimal("0.86")
                .multiply(req.powerKw())
                .divide(deltaT, 6, RoundingMode.HALF_UP);

        List<EquipmentRepository.BoilerBurnerPair> pairs =
                equipmentRepo.findBoilerBurnerPairs(req.powerKw(),
                        req.fuelType().name().toLowerCase(),
                        PageRequest.of(0, 20));

        List<ConfigurationCandidateDto> candidates = new ArrayList<>();

        for (EquipmentRepository.BoilerBurnerPair pair : pairs) {
            Integer dn = pair.getDnSize();
            if (dn == null) {
                continue;
            }

            Optional<Equipment> optPump = equipmentRepo.findCheapestPump(flow);
            Optional<Equipment> optValve = equipmentRepo.findCheapestValve(dn);
            Optional<Equipment> optFlowmeter = equipmentRepo.findCheapestFlowmeter(dn);

            if (optPump.isEmpty() || optValve.isEmpty() || optFlowmeter.isEmpty()) {
                continue;
            }

            Equipment boiler = equipmentRepo.findById(pair.getBoilerId()).orElse(null);
            Equipment burner = equipmentRepo.findById(pair.getBurnerId()).orElse(null);
            if (boiler == null || burner == null) {
                continue;
            }

            List<Equipment> bundle = new ArrayList<>(5);
            bundle.add(boiler);
            bundle.add(burner);
            bundle.add(optPump.get());
            bundle.add(optValve.get());
            bundle.add(optFlowmeter.get());

            if (includeAutomation) {
                equipmentRepo.findCheapestAutomation().ifPresent(bundle::add);
            }

            BigDecimal total = bundle.stream()
                    .map(e -> toPrice(e.getPrice()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            int maxDeliveryDays = bundle.stream()
                    .map(e -> e.getDeliveryDays() == null ? 0 : e.getDeliveryDays())
                    .max(Integer::compareTo)
                    .orElse(0);

            List<ConfigurationComponentDto> components = bundle.stream()
                    .map(equipmentMapper::toComponentDto)
                    .collect(Collectors.toList());

            ConfigurationCandidateDto candidate = ConfigurationCandidateDto.builder()
                    .requestId(req.id())
                    .totalPrice(total)
                    .currency("RUB")
                    .maxDeliveryDays(maxDeliveryDays)
                    .connectionKey(pair.getConnectionKey())
                    .dnSize(pair.getDnSize())
                    .components(components)
                    .build();

            candidates.add(candidate);
        }

        candidates.sort(Comparator
                .comparing(ConfigurationCandidateDto::totalPrice)
                .thenComparing(ConfigurationCandidateDto::maxDeliveryDays));

        if (candidates.size() > topN) {
            return candidates.subList(0, topN);
        }
        return candidates;
    }

    private void validate(HeatingRequestDto req) {
        if (req.powerKw() == null || req.powerKw().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("power_kw должен быть > 0");
        }
        if (req.tIn() == null || req.tOut() == null) {
            throw new IllegalArgumentException("t_in и t_out обязательны");
        }
        if (req.tIn().compareTo(req.tOut()) <= 0) {
            throw new IllegalArgumentException("t_in должен быть больше t_out");
        }
        if (req.fuelType() == null) {
            throw new IllegalArgumentException("fuel_type обязателен");
        }
    }

    private BigDecimal toPrice(BigDecimal p) {
        return p == null ? BigDecimal.ZERO : p;
    }
}