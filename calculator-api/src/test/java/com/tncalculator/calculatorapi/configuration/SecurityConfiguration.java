package com.tncalculator.calculatorapi.configuration;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
public class SecurityConfiguration {

    public static final String ADMIN_USER = "admin@tncalculator.com";
    public static final String CALCULATOR_USER = "calculator_user@tncalculator.com";

    @Bean
    public RestTemplate restTemplateTest() {
        RestTemplate rest = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        rest.getMessageConverters().add(0, mappingJacksonHttpMessageConverter());
//        rest.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        return rest;
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJacksonHttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(restTemplateObjectMapper());
        return converter;
    }

    @Bean
    public ObjectMapper restTemplateObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(MapperFeature.USE_ANNOTATIONS, false);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new Jdk8Module());
        return objectMapper;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/**");
    }

}
