package it.scoppelletti.springdemo.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping(path = "/private/user")
public class UserController {

    // This page isn't working
    // If the problem continues, contact the site owner.
    // HTTP ERROR 401
    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public Mono<String> home() {
        return Mono.just("user");
    }
}
