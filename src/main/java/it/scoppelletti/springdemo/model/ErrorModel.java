package it.scoppelletti.springdemo.model;

import lombok.Getter;
import lombok.ToString;

@ToString
public class ErrorModel {

    @Getter
    private final String className;

    @Getter
    private final String message;

    public ErrorModel(Throwable ex) {
        className = ex.getClass().getName();
        message = ex.getMessage();
    }
}
