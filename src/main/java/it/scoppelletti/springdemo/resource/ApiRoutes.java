package it.scoppelletti.springdemo.resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ApiRoutes {

    @Bean
    public RouterFunction<ServerResponse> routes(ApiHandler handler) {
        return RouterFunctions.route()
                .GET("/api/public/guest", handler::helloGuest)
                .GET("/api/private/user", handler::helloUser)
                .GET("/api/private/admin", handler::hellodmin)
                .build();
    }
}
