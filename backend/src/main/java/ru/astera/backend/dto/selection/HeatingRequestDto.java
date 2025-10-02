package ru.astera.backend.dto.selection;

import java.math.BigDecimal;
import java.util.UUID;

public class HeatingRequestDto {
    private UUID id;                 // может быть null, если запрос не сохранён в БД
    private BigDecimal powerKw;      // требуемая мощность, кВт
    private BigDecimal tIn;          // подача, °C
    private BigDecimal tOut;         // обратка, °C
    private FuelType fuelType;       // GAS/DIESEL/OTHER

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public BigDecimal getPowerKw() { return powerKw; }
    public void setPowerKw(BigDecimal powerKw) { this.powerKw = powerKw; }

    public BigDecimal getTIn() { return tIn; }
    public void setTIn(BigDecimal tIn) { this.tIn = tIn; }

    public BigDecimal getTOut() { return tOut; }
    public void setTOut(BigDecimal tOut) { this.tOut = tOut; }

    public FuelType getFuelType() { return fuelType; }
    public void setFuelType(FuelType fuelType) { this.fuelType = fuelType; }
}
