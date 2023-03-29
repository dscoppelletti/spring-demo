package it.scoppelletti.springdemo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class ExceptionController {
    // This works only for controllers

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String authentication(AuthenticationException ex) {
        // This is not called, likely because the exception has already been
        // handled by Spring Security Filters.
        log.error("Authentication exception.", ex);
        return "accessdenied";
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String accessDenied(AccessDeniedException ex) {
        // https://stackoverflow.com/questions/49137253
        log.error("Access denied exception.", ex);
        return "accessdenied";
    }
}
