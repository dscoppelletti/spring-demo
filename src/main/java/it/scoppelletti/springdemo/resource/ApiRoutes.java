package it.scoppelletti.springdemo.resource;

import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

// @Configuration
// The bean 'routes', defined in class path resource
// [it/scoppelletti/springdemo/resource/TutorialRoutes.class], could not be
// registered. A bean with that name has already been defined in class path
// resource [it/scoppelletti/springdemo/resource/ApiRoutes.class] and overriding
// is disabled.
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
