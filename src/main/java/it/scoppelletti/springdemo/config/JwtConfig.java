package it.scoppelletti.springdemo.config;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@ConfigurationProperties(prefix = "it.scoppelletti.spring-demo.security.jwt")
public record JwtConfig(

    @NotBlank
    String privateKeyLocation
) {

    @PostConstruct
    private void onInit() {
        log.info(toString());
    }
}
