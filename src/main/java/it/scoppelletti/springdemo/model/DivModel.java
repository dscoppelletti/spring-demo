package it.scoppelletti.springdemo.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class DivModel {

    @Getter
    @Setter
    private int numerator;

    @Getter
    @Setter
    private int denominator;
}
