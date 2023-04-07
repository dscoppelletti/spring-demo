package it.scoppelletti.springdemo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
// No ThymeLeaf ViewResolver with the following!
// @EnableWebFlux
public class DemoWebConfig implements WebFluxConfigurer {
    // No Thymeleaf ViewResolver if extends WebFluxConfigurationSupport!
}
