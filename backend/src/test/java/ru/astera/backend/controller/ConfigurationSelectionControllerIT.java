package ru.astera.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.astera.backend.config.TestContainersConfig;
import ru.astera.backend.entity.FuelType;
import ru.astera.backend.dto.selection.SelectionQueryDto;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestContainersConfig.class})
@WithMockUser(roles = "customer")
class ConfigurationSelectionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Проверяем успешный подбор с автоматикой (includeAutomation=true)
     * Каталог наполнен миграцией V2__seed_data.sql.
     * Для P=500 кВт, t_in=95, t_out=70, fuel=GAS:
     * - выбираются ARCUS KB-500 + KONORD G-500 (DN80, ключ DN80_GAS_STD)
     * - насос CNP NIS50-200 (дешевле WILO), клапан LD-DN80, расходомер Piterflow PF-DN80
     * - автоматика Siemens AUT-1
     * Итоговая цена ожидаемо около 1_295_000 RUB.
     */
    @Test
    void selectConfigurations_WithAutomation_Success() throws Exception {
        SelectionQueryDto q = new SelectionQueryDto();
        q.setPowerKw(new BigDecimal("500"));
        q.setTIn(new BigDecimal("95"));
        q.setTOut(new BigDecimal("70"));
        q.setFuelType(FuelType.gas);
        q.setTopN(5);
        q.setIncludeAutomation(true);

        mockMvc.perform(post("/api/selection/configurations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(q)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(Matchers.greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].currency").value("RUB"))
                .andExpect(jsonPath("$[0].dnSize").value(80))
                .andExpect(jsonPath("$[0].connectionKey").value("DN80_GAS_STD"))
                .andExpect(jsonPath("$[0].components[*].category",
                        Matchers.hasItems("boiler","burner","pump","valve","flowmeter","automation")))
                // цена в ожидаемом диапазоне (≈ 1_295_000)
                .andExpect(jsonPath("$[0].totalPrice",
                        Matchers.equalTo(1_295_000.00)));
    }

    /**
     * Без автоматики (includeAutomation=false) — цена ниже (~1_210_000 RUB).
     */
    @Test
    void selectConfigurations_WithoutAutomation_Success() throws Exception {
        SelectionQueryDto q = new SelectionQueryDto();
        q.setPowerKw(new BigDecimal("500"));
        q.setTIn(new BigDecimal("95"));
        q.setTOut(new BigDecimal("70"));
        q.setFuelType(FuelType.gas);
        q.setTopN(5);
        q.setIncludeAutomation(false);

        mockMvc.perform(post("/api/selection/configurations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(q)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(Matchers.greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].currency").value("RUB"))
                .andExpect(jsonPath("$[0].components[*].category",
                        Matchers.hasItems("boiler","burner","pump","valve","flowmeter")))
                .andExpect(jsonPath("$[0].components[*].category",
                        Matchers.not(Matchers.hasItem("automation"))))
                .andExpect(jsonPath("$[0].totalPrice",
                        Matchers.equalTo(1_210_000.00)));
    }

    @Test
    void selectConfigurations_InvalidDeltaT_BadRequest() throws Exception {
        SelectionQueryDto q = new SelectionQueryDto();
        q.setPowerKw(new BigDecimal("500"));
        q.setTIn(new BigDecimal("70"));
        q.setTOut(new BigDecimal("95"));
        q.setFuelType(FuelType.gas);

        mockMvc.perform(post("/api/selection/configurations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(q)))
                .andExpect(status().isBadRequest());
    }
}
