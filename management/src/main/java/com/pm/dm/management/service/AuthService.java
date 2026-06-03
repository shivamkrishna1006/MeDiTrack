package com.pm.dm.management.service;

import com.pm.dm.management.dto.auth.AuthResponse;
import com.pm.dm.management.dto.auth.LoginRequest;
import com.pm.dm.management.dto.auth.RefreshTokenRequest;
import com.pm.dm.management.dto.auth.RegisterRequest;
import com.pm.dm.management.exception.BadRequestException;
import com.pm.dm.management.exception.ConflictException;
import com.pm.dm.management.model.AppUser;
import com.pm.dm.management.model.Role;
import com.pm.dm.management.repository.AppUserRepository;
import com.pm.dm.management.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final AuditLogService auditLogService;

    public AuthService(AppUserRepository appUserRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager,
                       RefreshTokenService refreshTokenService,
                       AuditLogService auditLogService) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
        this.auditLogService = auditLogService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (appUserRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already exists");
        }
        if (appUserRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists");
        }
        validateRoleAssignment(request.getRole());

        AppUser user = new AppUser();
        user.setUsername(request.getUsername());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        AppUser savedUser = appUserRepository.save(user);
        auditLogService.log("USER_REGISTERED", "AppUser", savedUser.getId(), savedUser.getUsername());
        return buildAuthResponse(savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        AppUser user = appUserRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("User not found"));
        auditLogService.log("USER_LOGIN", "AppUser", user.getId(), user.getUsername());
        return buildAuthResponse(user);
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        String username = refreshTokenService.validateAndGetUsername(request.getRefreshToken());
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User not found"));
        return buildAuthResponse(user);
    }

    public void logout(RefreshTokenRequest request) {
        refreshTokenService.revoke(request.getRefreshToken());
    }

    private void validateRoleAssignment(Role requestedRole) {
        if (requestedRole == Role.ADMIN && appUserRepository.countByRole(Role.ADMIN) > 0) {
            throw new BadRequestException("Admin registration is locked after the first admin account");
        }
    }

    private AuthResponse buildAuthResponse(AppUser user) {
        AuthResponse response = new AuthResponse();
        response.setAccessToken(jwtService.generateToken(user));
        response.setRefreshToken(refreshTokenService.createToken(user));
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        return response;
    }
}
