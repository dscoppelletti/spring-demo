package it.scoppelletti.springdemo.service;

import it.scoppelletti.springdemo.LocalContext;
import it.scoppelletti.springdemo.model.JwtModel;
import java.time.Duration;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

@SpringBootTest
class JwtGeneratorTest {

    private final String TOKEN = """
        eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.\
        eyJzdWIiOiJqb2UiLCJleHAiOjE2ODI5ODU2MDAsImlhdCI6MTY4Mjg5OTIwMH0.\
        XKjJipadTCLk8diuGnXRWWk_R4Bs5Bi8_xfU5q20m7mbXZeHtN1c1Qabe6uZaMPv\
        uNaEN3HbQ08kM0uW4ghpmvKd2Ame2ExnYuQ3NTNKldw30sd_WdBnQB1DOD1R37Kr\
        2TDCsyyq5PdOZTuht97eAOXEgSmUV2g9On4OB3v2Nw15uwKFvsUsLCJNcC3s54tq\
        wpG9WoVvVODFmmIXNekra3Ci3RGTKlfSyE0wwh1OjDc70VIOs1KokjnW0TtA6rce\
        rC-qrZ4e-Yd634kqsFJrnExYcRF8vp4HMlRfpOV8kT64uGGUTzbopz7qwKYMSw0o\
        B4oRs9E-ZNhY9tUeM0SJWg""";

    @Autowired
    private JwtGenerator jwtGenerator;

    @Test
    void test() {
        var jwtModel = new JwtModel();
        jwtModel.setSubject("joe");
        jwtModel.setIssuedTime(LocalDateTime.of(2023, 5, 1, 0, 0, 0));
        jwtModel.setDuration(Duration.ofDays(1));

        Mono<String> token = jwtGenerator.generate(jwtModel,
                new LocalContext());
        Assertions.assertEquals(TOKEN, token.block());
    }
}
