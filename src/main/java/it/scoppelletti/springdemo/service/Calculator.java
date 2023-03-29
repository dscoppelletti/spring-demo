package it.scoppelletti.springdemo.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class Calculator {

    public Mono<Integer> div(int num, int den) {
        return Mono.just(num / den);
    }
}
