package it.scoppelletti.springdemo.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import it.scoppelletti.springdemo.ApplicationException;
import it.scoppelletti.springdemo.LocalContext;
import it.scoppelletti.springdemo.config.JwtConfig;
import it.scoppelletti.springdemo.model.JwtModel;
import jakarta.annotation.Nonnull;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.ParseException;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/*
 * Key Pair Generation
 *
 * % openssl genrsa -out rsaPrivateKey.pem 2048
 * % openssl rsa -pubout -in rsaPrivateKey.pem -out publicKey.pem
 * % openssl pkcs8 -topk8 -nocrypt -inform pem -in rsaPrivateKey.pem \
 * > -outform der -out privateKey.p8
 */

@Service
@RequiredArgsConstructor
public class JwtGenerator {

    private static final int BUFSIZ = 256;
    private static final String ALG = "RSA";
    private final JwtConfig config;
    private final ResourceLoader resourceLoader;

    @Nonnull
    public Mono<String> generate(@Nonnull JwtModel jwtModel,
            @Nonnull LocalContext localCtx) {
        Objects.requireNonNull(jwtModel, "Argument jwtModel is null.");
        Objects.requireNonNull(localCtx, "Argument localCtx is null");

        return Mono.zip(loadHeader(localCtx), loadClaims(jwtModel),
                loadPrivateKey(localCtx))
                .map(parts -> {
                    var jwt = new SignedJWT(parts.getT1(), parts.getT2());
                    var signer = new RSASSASigner(parts.getT3());

                    try {
                        jwt.sign(signer);
                    } catch (JOSEException ex) {
                        throw new ApplicationException(localCtx.getMessage(
                                "error.jwt"), ex);
                    }

                    return jwt;
                })
                .map(JWSObject::serialize);
    }

    private Mono<JWSHeader> loadHeader(LocalContext localCtx) {
        return Mono.fromSupplier(() -> {
            Map<String, Object> headerMap = new HashMap<>();
            headerMap.put("alg", JWSAlgorithm.RS256.getName());
            headerMap.put("typ", JOSEObjectType.JWT.getType());

            try {
                return JWSHeader.parse(headerMap);
            } catch (ParseException ex) {
                throw new ApplicationException(localCtx.getMessage("error.jwt"),
                        ex);
            }
        });
    }

    private Mono<JWTClaimsSet> loadClaims(JwtModel jwtModel) {
        return Mono.fromSupplier(() -> {
            var expirationTime = jwtModel.getIssuedTime().plus(
                    jwtModel.getDuration());
            var builder = new JWTClaimsSet.Builder()
                    .subject(jwtModel.getSubject())
                    .issueTime(Date.from(
                            jwtModel.getIssuedTime().toInstant(ZoneOffset.UTC)))
                    .expirationTime(Date.from(
                            expirationTime.toInstant(ZoneOffset.UTC)));
            if (StringUtils.isNotBlank(jwtModel.getScope())) {
                builder.claim("scope", jwtModel.getScope());
            }

            return builder.build();
        });
    }

    private Mono<PrivateKey> loadPrivateKey(LocalContext localCtx) {
        // https://stackoverflow.com/questions/3243018

        Resource res = resourceLoader.getResource(config.privateKeyLocation());
        Flux<DataBuffer> privateKeyFlux = DataBufferUtils.read(
                res, DefaultDataBufferFactory.sharedInstance, BUFSIZ);
        Mono<KeySpec> keySpecMono = DataBufferUtils.join(privateKeyFlux)
                .map(dataBuf -> {
                    byte[] keyBuf = new byte[dataBuf.readableByteCount()];
                    dataBuf.read(keyBuf);
                    DataBufferUtils.release(dataBuf);
                    return new PKCS8EncodedKeySpec(keyBuf);
                });

        Mono<KeyFactory> keyFactoryMono = Mono.fromSupplier(() -> {
            try {
                return KeyFactory.getInstance(ALG);
            } catch (NoSuchAlgorithmException ex) {
                throw new ApplicationException(localCtx.getMessage("error.jwt"),
                        ex);
            }
        });

        return Mono.zip(keySpecMono, keyFactoryMono, (keySpec, keyFactory) -> {
            try {
                return keyFactory.generatePrivate(keySpec);
            } catch (InvalidKeySpecException ex) {
                throw new ApplicationException(localCtx.getMessage("error.jwt"),
                        ex);
            }
        });
    }
}
