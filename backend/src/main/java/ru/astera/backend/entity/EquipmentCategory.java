package ru.astera.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EquipmentCategory {
    boiler("Котел"),
    burner("Горелка"),
    pump("Насос"),
    valve("Клапан"),
    flowmeter("Расходомер"),
    automation("Автоматика");

    private final String displayName;
}
