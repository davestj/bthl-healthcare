package com.bthl.healthcare.controller;

import com.bthl.healthcare.dto.UserRegistrationDto;
import com.bthl.healthcare.model.enums.UserType;
import com.bthl.healthcare.security.jwt.JwtTokenProvider;
import com.bthl.healthcare.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void registerUser_returnsBadRequestWhenPasswordsDoNotMatch() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        dto.setPassword("Password123!");
        dto.setConfirmPassword("Mismatch123!");
        dto.setFirstName("Test");
        dto.setLastName("User");
        dto.setUserType(UserType.COMPANY_USER);
        dto.setAcceptTerms(true);
        dto.setAcceptPrivacyPolicy(true);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
