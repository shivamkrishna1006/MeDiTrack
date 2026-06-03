package com.pm.dm.management.service;

import com.pm.dm.management.dto.auth.LoginRequest;
import com.pm.dm.management.dto.auth.RefreshTokenRequest;
import com.pm.dm.management.dto.auth.RegisterRequest;
import com.pm.dm.management.model.Role;
import com.pm.dm.management.repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@Transactional
class AuthServiceTests {

    @Autowired
    private AuthService authService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Test
    void shouldRegisterAndLoginReceptionist() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("reception1");
        registerRequest.setFullName("Reception One");
        registerRequest.setEmail("reception1@example.com");
        registerRequest.setPassword("Password@123");
        registerRequest.setRole(Role.RECEPTIONIST);

        var registerResponse = authService.register(registerRequest);

        assertThat(registerResponse.getAccessToken()).isNotBlank();
        assertThat(appUserRepository.existsByUsername("reception1")).isTrue();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("reception1");
        loginRequest.setPassword("Password@123");

        var loginResponse = authService.login(loginRequest);
        assertThat(loginResponse.getAccessToken()).isNotBlank();
        assertThat(loginResponse.getRefreshToken()).isNotBlank();
        assertThat(loginResponse.getRole()).isEqualTo(Role.RECEPTIONIST);

        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setRefreshToken(loginResponse.getRefreshToken());

        var refreshResponse = authService.refresh(refreshTokenRequest);
        assertThat(refreshResponse.getAccessToken()).isNotBlank();
        assertThat(refreshResponse.getRefreshToken()).isNotBlank();
    }
}
