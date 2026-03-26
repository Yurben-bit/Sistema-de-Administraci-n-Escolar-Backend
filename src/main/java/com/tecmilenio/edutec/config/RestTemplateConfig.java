package com.tecmilenio.edutec.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        // Tiempo máximo para establecer conexión (5 segundos).
        factory.setConnectTimeout(5000);

        // Tiempo máximo esperando respuesta una vez conectado (10 segundos).
        factory.setReadTimeout(10000);

        return new RestTemplate(factory);
    }
}