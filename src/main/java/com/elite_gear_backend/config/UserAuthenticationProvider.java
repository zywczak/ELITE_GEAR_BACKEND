package com.elite_gear_backend.config;

import java.util.Arrays;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.elite_gear_backend.dto.UserDTO;
import com.elite_gear_backend.services.UserService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class UserAuthenticationProvider {

    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    private final UserService userService;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(UserDTO user) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + 3600000);
    
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        return JWT.create()
                .withSubject(user.getEmail())
                .withIssuedAt(now)
                .withExpiresAt(validity)
                
                .withClaim("type", user.getType().name())
                .withClaim("name", user.getName())
                .withClaim("surname", user.getSurname())
                .withClaim("id", user.getId())
                .sign(algorithm);
    }    

    public Authentication validateToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        JWTVerifier verifier = JWT.require(algorithm).build();

        DecodedJWT decoded = verifier.verify(token);

        UserDTO user = userService.findByEmail(decoded.getSubject());

        return new UsernamePasswordAuthenticationToken(user, null, Arrays.asList(new SimpleGrantedAuthority("ROLE_" + user.getType())));
    }
}