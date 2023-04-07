package it.scoppelletti.springdemo.r2dbc;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

@ToString
public class Tutorial {

    @Id
    @Getter
    @Setter
    private int id;

    @Getter
    @Setter
    @NotBlank
    private String title;

    @Getter
    @Setter
    @NotBlank
    private String description;

    @Getter
    @Setter
    private boolean published;
}

