package com.heimhel.notes.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper om = new ObjectMapper();
        // поддержка java.time (Instant/LocalDateTime и т.д.)
        om.registerModule(new JavaTimeModule());
        // не сериализовать даты как числа
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // можно настроить дальше: пропуски нулей, десериализаторы, стратегии и т.д.
        return om;
    }

}
