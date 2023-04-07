package it.scoppelletti.springdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class DemoConfig {

    @Bean
    public LocalValidatorFactoryBean validator() {
        // http://docs.spring.io/spring-framework/docs/current/reference/html/core.html#validation-beanvalidation-spring
        return new LocalValidatorFactoryBean();
    }
}
