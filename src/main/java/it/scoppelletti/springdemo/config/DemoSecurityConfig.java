package it.scoppelletti.springdemo.config;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity(useAuthorizationManager = true)
public class DemoSecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityWebFilterChain(
            ServerHttpSecurity http) {
        http.authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/private/**").authenticated()
                        .pathMatchers("/private/**").authenticated()
                        .anyExchange().permitAll())
// Avoid "An expected CSRF token cannot be found
// http://docs.spring.io/spring-security/site/docs/5.2.5.RELEASE/reference/html/protection-against-exploits-2.html
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .exceptionHandling()
                    .authenticationEntryPoint(new AuthenticationEntryPoint())
//                    .accessDeniedHandler(new AccessDeniedHandler())
//                .accessDeniedHandler((exchange, exception) ->
//                        Mono.error(exception))
                .and()
//                .oauth2ResourceServer(
//                        ServerHttpSecurity.OAuth2ResourceServerSpec::jwt);
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt ->
                                jwt.jwtAuthenticationConverter(
                                        grantedAuthoritiesExtractor())
                        )
                );

        return http.build();
    }

    private Converter<Jwt, Mono<AbstractAuthenticationToken>>
    grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter jwtAuthenticationConverter =
                new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
                new GrantedAuthoritiesExtractor());
        return new ReactiveJwtAuthenticationConverterAdapter(
                jwtAuthenticationConverter);
    }

    private static class AuthenticationEntryPoint implements
            ServerAuthenticationEntryPoint {
        // This works for both controllers and APIs.

        @Override
        public Mono<Void> commence(ServerWebExchange exchange,
                AuthenticationException ex) {
            if (isApi(exchange.getRequest())) {
                return Mono.error(ex);
            }

            log.error("Authentication exception.", ex);
            var resp = exchange.getResponse();
            resp.setStatusCode(HttpStatus.PERMANENT_REDIRECT);
            String baseUrl = exchange.getRequest().getPath()
                    .contextPath().value();
            resp.getHeaders().setLocation(URI.create(
                    baseUrl.concat("/jwtgenerator")));
            return resp.setComplete();
        }

        /**
         * Verify if a request is for an API.
         *
         * @param  req Request.
         * @return     Whether the request is for an API.
         */
        private boolean isApi(ServerHttpRequest req) {
            List<PathContainer.Element> elements;

            elements = req.getPath().pathWithinApplication().elements();
            if (elements.size() < 2) {
                return false;
            }

            // "/", "api", "/", ...
            return StringUtils.equals(elements.get(1).value(), "api");
        }
    }

    private static class AccessDeniedHandler implements
            ServerAccessDeniedHandler {
        // https://stackoverflow.com/questions/57853619
        // This works for both controllers and APIs...

        @Override
        public Mono<Void> handle(ServerWebExchange exchange,
                AccessDeniedException ex) {
            log.error("Access denied exception.", ex);
            var resp = exchange.getResponse();

            // ... but the following is ignored: the status code will be 200
            // and the location header will be ignored though set
            resp.setStatusCode(HttpStatus.PERMANENT_REDIRECT);

            String baseUrl = exchange.getRequest().getPath()
                    .contextPath().value();
            resp.getHeaders().setLocation(URI.create(
                    baseUrl.concat("/error/accessdenied")));
            return resp.setComplete();
        }
    }

    /**
     * No add "SCOPE_" prefix to the scopes.
     */
    private static class GrantedAuthoritiesExtractor
            implements Converter<Jwt, Collection<GrantedAuthority>> {

        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            String scope = Objects.toString(jwt.getClaims()
                    .getOrDefault("scope", StringUtils.EMPTY));

            String[] scopes = scope.split("\\s+");

            return Arrays.stream(scopes)
                    .filter(StringUtils::isNotBlank)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
    }
}
