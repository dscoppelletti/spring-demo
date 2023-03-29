package it.scoppelletti.springdemo.controller;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
@PreAuthorize("hasAuthority('admin')")
@RequestMapping(path = "/private/admin")
public class AdminController {

    // Access to localhost was denied
    // You don't have authorisation to view this page.
    // HTTP ERROR 403
    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public Mono<String> home() {
        return Mono.just("admin");
    }
}
