package com.tcon.careers.service;

import com.tcon.careers.model.User;
import com.tcon.careers.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataInitializationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.default-username}")
    private String defaultAdminEmail;

    @Value("${app.admin.default-password}")
    private String defaultAdminPassword;

    @PostConstruct
    public void initializeDefaultData() {
        createDefaultAdminIfNotExists();
    }

    private void createDefaultAdminIfNotExists() {
        if (!userRepository.existsByEmail(defaultAdminEmail)) {
            User defaultAdmin = User.builder()
                    .email(defaultAdminEmail)
                    .password(passwordEncoder.encode(defaultAdminPassword))
                    .firstName("Admin")
                    .lastName("TCON")
                    .role("ROLE_ADMIN")
                    .enabled(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            userRepository.save(defaultAdmin);
            log.info("✅ Default admin user created successfully: {}", defaultAdminEmail);
            log.info("⚠️  Please change the default admin password for security!");
        } else {
            log.info("Default admin user already exists: {}", defaultAdminEmail);
        }
    }
}

