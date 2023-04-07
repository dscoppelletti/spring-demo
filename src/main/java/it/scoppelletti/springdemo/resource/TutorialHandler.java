package it.scoppelletti.springdemo.resource;

import it.scoppelletti.springdemo.r2dbc.Tutorial;
import it.scoppelletti.springdemo.r2dbc.TutorialService;
import it.scoppelletti.springdemo.r2dbc.TutorialValidator;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class TutorialHandler {
    // http://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html#webflux-fn-handler-classes

// http://github.com/spring-projects/spring-framework/issues/29462
// ProblemDetail/ErrorResponse (RFC 7807) not supported by @AdviceController
// (neither @RestAdviceController) neither
// spring.webflux.problemdetails.enabled: true.
//{
//    "timestamp": "2023-04-06T15:08:48.559+00:00",
//    "path": "/api/tutorials",
//    "status": 415,
//    "error": "Unsupported Media Type",
//    "requestId": "3dc5df7b-1"
//}
//{
//    "timestamp": "2023-04-06T15:10:52.728+00:00",
//    "path": "/api/tutorials",
//    "status": 500,
//    "error": "Internal Server Error",
//    "requestId": "3dc5df7b-2"
//}

    private final TutorialService tutorialService;
    private final Validator validator;

    public Mono<ServerResponse> getAllTutorial(ServerRequest req) {
        Flux<Tutorial> list;
        Optional<String> title = req.queryParam("title");

        if (title.isPresent()) {
            list = tutorialService.findByTitleContaining(title.get());
        } else {
            list = tutorialService.findAll();
        }

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(list, Tutorial.class);
    }

    public Mono<ServerResponse> getTutorialById(ServerRequest req) {
        int id;

        try {
            id = Integer.parseInt(req.pathVariable("id"));
        } catch (NumberFormatException ex) {
            throw new ServerWebInputException(ex.getMessage());
//            {
//                "timestamp": "2023-04-06T14:50:42.776+00:00",
//                    "path": "/api/tutorials/a",
//                    "status": 400,
//                    "error": "Bad Request",
//                    "requestId": "50389fe7-6"
//            }
        }

        return tutorialService.findById(id)
                .flatMap(tutorial -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(tutorial))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> createTutorial(ServerRequest req) {
        return req.bodyToMono(Tutorial.class)
                .doOnNext(this::validate)
                .flatMap(tutorialService::save)
                .flatMap(tutorial -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(tutorial));
    }

    public Mono<ServerResponse> updateTutorial(ServerRequest req) {
        int id;

        try {
            id = Integer.parseInt(req.pathVariable("id"));
        } catch (NumberFormatException ex) {
            throw new ServerWebInputException(ex.getMessage());
        }

        return req.bodyToMono(Tutorial.class)
                .doOnNext(this::validate)
                .flatMap(tutorial ->
                        tutorialService.update(id, tutorial))
                .flatMap(tutorial -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(tutorial));
    }

    private void validate(Tutorial tutorial) {
        Errors errors = new BeanPropertyBindingResult(tutorial, "tutorial");
        validator.validate(tutorial, errors);
        if (errors.hasErrors()) {
            log.error(errors.toString());
            throw new ServerWebInputException(errors.toString());
        }
    }

//    private void validate(Tutorial tutorial) {
//        Validator validator = new TutorialValidator();
//
//        Errors errors = new BeanPropertyBindingResult(tutorial, "tutorial");
//        validator.validate(tutorial, errors);
//        if (errors.hasErrors()) {
//            throw new ServerWebInputException(errors.toString());
//        }
//    }

    public Mono<ServerResponse> deleteTutorial(ServerRequest req) {
        int id;

        try {
            id = Integer.parseInt(req.pathVariable("id"));
        } catch (NumberFormatException ex) {
            throw new ServerWebInputException(ex.getMessage());
        }

        return tutorialService.deleteById(id)
                .flatMap(nu -> ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> deleteAllTutorials(ServerRequest req) {
        return tutorialService.deleteAll()
                .flatMap(nu -> ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> findByPublished(ServerRequest req) {
        Flux<Tutorial> list = tutorialService.findByPublished(true);
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(list, Tutorial.class);
    }
}
