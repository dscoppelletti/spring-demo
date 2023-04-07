package it.scoppelletti.springdemo.resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class TutorialRoutes {

    @Bean
    public RouterFunction<ServerResponse> routes(
            TutorialHandler handler) {
        return RouterFunctions.route()
                .GET("/api/tutorials",
                        RequestPredicates.accept(MediaType.APPLICATION_JSON),
                        handler::getAllTutorial)
                // Order is important:
                // If you move GET /api/tutorials/published after GET
                // /api/tutorials/{id}, when you request GET
                // /api/tutorials/published, it is routed to GET
                // /api/tutorials/{id}.
                .GET("/api/tutorials/published",
                        RequestPredicates.accept(MediaType.APPLICATION_JSON),
                        handler::findByPublished)
                .GET("/api/tutorials/{id}",
                        RequestPredicates.accept(MediaType.APPLICATION_JSON),
                        handler::getTutorialById)
                .POST("/api/tutorials",
                        RequestPredicates.accept(MediaType.APPLICATION_JSON),
                        handler::createTutorial)
                .PUT("/api/tutorials/{id}",
                        RequestPredicates.accept(MediaType.APPLICATION_JSON),
                        handler::updateTutorial)
                .DELETE("/api/tutorials/{id}",
                        RequestPredicates.accept(MediaType.APPLICATION_JSON),
                        handler::deleteTutorial)
                .DELETE("/api/tutorials",
                        RequestPredicates.accept(MediaType.APPLICATION_JSON),
                        handler::deleteAllTutorials)
                .build();
    }
}


