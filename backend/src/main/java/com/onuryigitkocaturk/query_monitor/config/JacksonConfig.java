package com.onuryigitkocaturk.query_monitor.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// otomatik gelmeyen yerlerde ObjectMapper'ı elle tanımlayıp inject etmek için
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
