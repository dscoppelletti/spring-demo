package it.scoppelletti.springdemo.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class JwtModel {

    @Getter
    @Setter
    @NotBlank
    private String subject;

    @Getter
    @Setter
    @NotNull
    private LocalDateTime issuedTime;

    @Getter
    @Setter
    @NotNull
    private Duration duration;

    @Getter
    @Setter
    private String scope;
}
