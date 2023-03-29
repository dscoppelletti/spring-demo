package it.scoppelletti.springdemo.controller;

import it.scoppelletti.springdemo.model.DivModel;
import it.scoppelletti.springdemo.model.ErrorModel;
import it.scoppelletti.springdemo.service.Calculator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/divcalculator")
public class DivController {

    private final Calculator calculator;

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public Mono<String> home(DivModel divModel) {
        return Mono.just("divcalculator");
    }

    @PostMapping(produces = MediaType.TEXT_HTML_VALUE)
    public Mono<String> divide(@Valid DivModel divModel,
            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return Mono.just("divcalculator");
        }

        try {
            Mono<Integer> result = calculator.div(divModel.getNumerator(),
                    divModel.getDenominator());
            model.addAttribute("result", result);
        } catch (RuntimeException ex) {
            log.error("Division failed", ex);
            model.addAttribute("errorModel", new ErrorModel(ex));
        }

        return Mono.just("divcalculator");
    }
}
