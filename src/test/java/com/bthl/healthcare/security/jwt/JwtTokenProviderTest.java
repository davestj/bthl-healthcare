package com.bthl.healthcare.security.jwt;

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
