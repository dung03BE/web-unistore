package com.dung.UniStore.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

@Configuration
public class ConfigBean {
    @Bean
    public ModelMapper init() {
        return new ModelMapper();
    }
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}