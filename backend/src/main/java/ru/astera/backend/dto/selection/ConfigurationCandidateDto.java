package ru.astera.backend.dto.selection;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConfigurationCandidateDto {
    private UUID requestId;               // может быть null
    private BigDecimal totalPrice;
    private String currency;              // "RUB"
    private Integer maxDeliveryDays;      // максимум по компонентам
    private String connectionKey;         // для котёл↔горелка
    private Integer dnSize;               // DN по котлу/линии

    private List<ConfigurationComponentDto> components = new ArrayList<>();

    public UUID getRequestId() { return requestId; }
    public void setRequestId(UUID requestId) { this.requestId = requestId; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Integer getMaxDeliveryDays() { return maxDeliveryDays; }
    public void setMaxDeliveryDays(Integer maxDeliveryDays) { this.maxDeliveryDays = maxDeliveryDays; }

    public String getConnectionKey() { return connectionKey; }
    public void setConnectionKey(String connectionKey) { this.connectionKey = connectionKey; }

    public Integer getDnSize() { return dnSize; }
    public void setDnSize(Integer dnSize) { this.dnSize = dnSize; }

    public List<ConfigurationComponentDto> getComponents() { return components; }
    public void setComponents(List<ConfigurationComponentDto> components) { this.components = components; }
}
