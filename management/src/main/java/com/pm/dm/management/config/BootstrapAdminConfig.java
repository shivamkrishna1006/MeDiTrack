package com.pm.dm.management.config;

import com.pm.dm.management.model.AppUser;
import com.pm.dm.management.model.Role;
import com.pm.dm.management.repository.AppUserRepository;
import com.pm.dm.management.service.AuditLogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BootstrapAdminConfig {

    @Bean
    CommandLineRunner bootstrapAdmin(AppUserRepository appUserRepository,
                                     PasswordEncoder passwordEncoder,
                                     AuditLogService auditLogService,
                                     @Value("${app.bootstrap-admin.enabled}") boolean enabled,
                                     @Value("${app.bootstrap-admin.username}") String username,
                                     @Value("${app.bootstrap-admin.full-name}") String fullName,
                                     @Value("${app.bootstrap-admin.email}") String email,
                                     @Value("${app.bootstrap-admin.password}") String password) {
        return args -> {
            if (!enabled || appUserRepository.existsByUsername(username)) {
                return;
            }
            AppUser admin = new AppUser();
            admin.setUsername(username);
            admin.setFullName(fullName);
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRole(Role.ADMIN);
            AppUser saved = appUserRepository.save(admin);
            auditLogService.log("ADMIN_BOOTSTRAPPED", "AppUser", saved.getId(), username);
        };
    }
}
