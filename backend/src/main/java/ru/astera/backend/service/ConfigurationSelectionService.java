package ru.astera.backend.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.astera.backend.dto.selection.ConfigurationCandidateDto;
import ru.astera.backend.dto.selection.ConfigurationComponentDto;
import ru.astera.backend.dto.selection.HeatingRequestDto;
import ru.astera.backend.entity.Equipment;
import ru.astera.backend.mapper.EquipmentMapper;
import ru.astera.backend.repository.EquipmentRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConfigurationSelectionService {

    private final EquipmentRepository equipmentRepo;
    private final EquipmentMapper equipmentMapper;

    public ConfigurationSelectionService(EquipmentRepository equipmentRepo,
                                         EquipmentMapper equipmentMapper) {
        this.equipmentRepo = equipmentRepo;
        this.equipmentMapper = equipmentMapper;
    }

    /**
     * Главный метод подбора конфигураций.
     *
     * @param req               входные параметры (мощность, t_in/t_out, топливо и т.д.)
     * @param topN              сколько лучших конфигураций вернуть (например, 5)
     * @param includeAutomation включать ли автоматику как дополнительный компонент
     * @return список кандидатов, отсортированных по возрастанию полной цены (и далее по сроку поставки)
     */
    @Transactional(readOnly = true)
    public List<ConfigurationCandidateDto> selectTopConfigurations(HeatingRequestDto req,
                                                                   int topN,
                                                                   boolean includeAutomation) {
        validate(req);

        // 1) считаем требуемый расход (м³/ч): Q = 0.86 * P(kW) / (t_in - t_out)
        BigDecimal deltaT = req.getTIn().subtract(req.getTOut());
        BigDecimal flow = new BigDecimal("0.86")
                .multiply(req.getPowerKw())
                .divide(deltaT, 6, RoundingMode.HALF_UP);

        // 2) берём топ-20 (или меньше) пар "котёл+горелка" по цене (оптимально для последующего комбинаторного шага)
        List<EquipmentRepository.BoilerBurnerPair> pairs =
                equipmentRepo.findBoilerBurnerPairs(req.getPowerKw(),
                        req.getFuelType().name().toLowerCase(),
                        PageRequest.of(0, 20));

        List<ConfigurationCandidateDto> candidates = new ArrayList<>();

        for (EquipmentRepository.BoilerBurnerPair pair : pairs) {
            // 3) минимально "дорогие" насос, вентиль и расходомер под наш расход/DN
            Integer dn = pair.getDnSize();
            if (dn == null) {
                continue;
            }

            Optional<Equipment> optPump      = equipmentRepo.findCheapestPump(flow);
            Optional<Equipment> optValve     = equipmentRepo.findCheapestValve(dn);
            Optional<Equipment> optFlowmeter = equipmentRepo.findCheapestFlowmeter(dn);

            if (optPump.isEmpty() || optValve.isEmpty() || optFlowmeter.isEmpty()) {
                // неполный сет — пропускаем
                continue;
            }

            // подтянем полные entities для котла/горелки (для бренд/модель и сроков)
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

            // автоматика — по желанию
            if (includeAutomation) {
                equipmentRepo.findCheapestAutomation().ifPresent(bundle::add);
            }

            // 4) считаем итоги
            BigDecimal total = bundle.stream()
                    .map(e -> toPrice(e.getPrice()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            int maxDeliveryDays = bundle.stream()
                    .map(e -> e.getDeliveryDays() == null ? 0 : e.getDeliveryDays())
                    .max(Integer::compareTo)
                    .orElse(0);

            // 5) собираем DTO кандидата
            List<ConfigurationComponentDto> components = bundle.stream()
                    .map(equipmentMapper::toComponentDto) // маппер -> заполняет category/brand/model/qty=1/price/subtotal
                    .collect(Collectors.toList());

            ConfigurationCandidateDto candidate = new ConfigurationCandidateDto();
            candidate.setRequestId(req.getId()); // при необходимости
            candidate.setTotalPrice(total);
            candidate.setCurrency("RUB");
            candidate.setMaxDeliveryDays(maxDeliveryDays);
            candidate.setConnectionKey(pair.getConnectionKey());
            candidate.setDnSize(dn);
            candidate.setComponents(components);

            candidates.add(candidate);
        }

        // 6) ранжируем: по цене, затем по сроку поставки
        candidates.sort(Comparator
                .comparing(ConfigurationCandidateDto::getTotalPrice)
                .thenComparing(ConfigurationCandidateDto::getMaxDeliveryDays));

        // 7) отдаём top-N
        if (candidates.size() > topN) {
            return candidates.subList(0, topN);
        }
        return candidates;
    }

    private void validate(HeatingRequestDto req) {
        if (req.getPowerKw() == null || req.getPowerKw().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("power_kw должен быть > 0");
        }
        if (req.getTIn() == null || req.getTOut() == null) {
            throw new IllegalArgumentException("t_in и t_out обязательны");
        }
        if (req.getTIn().compareTo(req.getTOut()) <= 0) {
            throw new IllegalArgumentException("t_in должен быть больше t_out");
        }
        if (req.getFuelType() == null) {
            throw new IllegalArgumentException("fuel_type обязателен");
        }
    }

    private BigDecimal toPrice(BigDecimal p) {
        return p == null ? BigDecimal.ZERO : p;
    }
}