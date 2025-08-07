package com.bthl.healthcare.security.jwt;

import com.bthl.healthcare.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class JwtTokenProviderTest {

    @Test
    void validateTokenShouldReturnFalseForTamperedToken() {
        JwtTokenProvider provider = new JwtTokenProvider();
        ReflectionTestUtils.setField(provider, "jwtSecret", "test-secret");
        ReflectionTestUtils.setField(provider, "jwtExpirationInMs", 3600000);

        User user = new User();
        user.id = UUID.randomUUID();
        user.username = "tester";
        user.passwordHash = "password";
        user.email = "tester@example.com";

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user,
                null,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String token = provider.createToken(authentication);

        String invalidToken = token.substring(0, 1).replace(token.charAt(0), 'a') + token.substring(1);

        assertFalse(provider.validateToken(invalidToken));

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;
    private final String secret = "test-secret";

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(tokenProvider, "jwtSecret", secret);
        ReflectionTestUtils.setField(tokenProvider, "jwtExpirationInMs", 3600000);
    }

    @Test
    void validateTokenReturnsFalseForTamperedToken() {
        String validToken = JWT.create()
                .withSubject("user-id")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600000))
                .withIssuer("bthl-healthcare")
                .sign(Algorithm.HMAC512(secret));

        char firstChar = validToken.charAt(0) != 'a' ? 'a' : 'b';
        String tamperedToken = firstChar + validToken.substring(1);

        assertThat(tokenProvider.validateToken(validToken)).isTrue();
        assertThat(tokenProvider.validateToken(tamperedToken)).isFalse();
    }
}
