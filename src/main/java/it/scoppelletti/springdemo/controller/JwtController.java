package it.scoppelletti.springdemo.controller;

import it.scoppelletti.springdemo.LocalContext;
import it.scoppelletti.springdemo.model.JwtModel;
import it.scoppelletti.springdemo.service.JwtGenerator;
import jakarta.validation.Valid;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/jwtgenerator")
public class JwtController {

    private final JwtGenerator jwtGenerator;
    private final MessageSource messageSource;

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public Mono<String> home(JwtModel jwtModel) {
        return Mono.just("jwtgenerator");
    }

    @PostMapping(produces = MediaType.TEXT_HTML_VALUE)
    public Mono<String> generate(@Valid JwtModel jwtModel,
            BindingResult bindingResult, Model model, Locale locale) {
        if (bindingResult.hasErrors()) {
            return Mono.just("jwtgenerator");
        }

        Mono<String> token = jwtGenerator.generate(jwtModel,
                LocalContext.builder()
                        .locale(locale)
                        .messageSource(messageSource)
                        .build());

        model.addAttribute("jwt", token);
        return Mono.just("jwtgenerator");
    }
}
