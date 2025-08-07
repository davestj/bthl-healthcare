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
    }
}
