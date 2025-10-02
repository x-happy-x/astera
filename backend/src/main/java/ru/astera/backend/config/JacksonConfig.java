package ru.astera.backend.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder
                .featuresToDisable(
                        SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
                )
                .featuresToEnable(
                        SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN,
                        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
                )
                .timeZone(java.util.TimeZone.getTimeZone("UTC"))
                .modules(new JavaTimeModule())
                .serializationInclusion(JsonInclude.Include.NON_NULL);
    }
}