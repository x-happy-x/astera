package ru.astera.backend.dto.selection;

import java.math.BigDecimal;

public class SelectionQueryDto {
    private BigDecimal powerKw;
    private BigDecimal tIn;
    private BigDecimal tOut;
    private FuelType fuelType;

    private Integer topN = 3;
    private Boolean includeAutomation = true;

    public BigDecimal getPowerKw() { return powerKw; }
    public void setPowerKw(BigDecimal powerKw) { this.powerKw = powerKw; }

    public BigDecimal getTIn() { return tIn; }
    public void setTIn(BigDecimal tIn) { this.tIn = tIn; }

    public BigDecimal getTOut() { return tOut; }
    public void setTOut(BigDecimal tOut) { this.tOut = tOut; }

    public FuelType getFuelType() { return fuelType; }
    public void setFuelType(FuelType fuelType) { this.fuelType = fuelType; }

    public Integer getTopN() { return topN; }
    public void setTopN(Integer topN) { this.topN = topN; }

    public Boolean getIncludeAutomation() { return includeAutomation; }
    public void setIncludeAutomation(Boolean includeAutomation) { this.includeAutomation = includeAutomation; }
}
