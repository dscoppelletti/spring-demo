package it.scoppelletti.springdemo.resource;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ApiHandler {

    public Mono<ServerResponse> helloGuest(ServerRequest req) {
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(Mono.just("Hello, guest!"), String.class);
    }

    public Mono<ServerResponse> helloUser(ServerRequest req) {
        // < HTTP/1.1 401 Unauthorized
        // < WWW-Authenticate: Bearer
        Mono<String> greetings = req.principal()
                .map(principal -> String.format("Hello, %1$s!",
                        principal.getName()));
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(greetings, String.class);
    }

    @PreAuthorize("hasAuthority('admin')")
    public Mono<ServerResponse> hellodmin(ServerRequest req) {
        // < HTTP/1.1 403 Forbidden
        // < WWW-Authenticate: Bearer error="insufficient_scope",
        // error_description="The request requires higher privileges than
        // provided by the access token.",
        // error_uri="https://tools.ietf.org/html/rfc6750#section-3.1"
        Mono<String> greetings = req.principal()
                .map(principal -> String.format("Hello, %1$s!",
                        principal.getName()));
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(greetings, String.class);
    }
}
