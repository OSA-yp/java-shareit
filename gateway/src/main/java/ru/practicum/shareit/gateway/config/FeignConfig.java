package ru.practicum.shareit.gateway.config;

import feign.Client;
import feign.hc5.ApacheHttp5Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    // Переопределен клиент, так как используемый по-умолчанию не поддерживает PATCH
    @Bean
    public Client feignClient() {
        return new ApacheHttp5Client();
    }
}