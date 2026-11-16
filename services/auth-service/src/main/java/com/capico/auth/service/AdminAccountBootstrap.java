package com.capico.auth.service;

import com.capico.auth.entity.AppUser;
import com.capico.auth.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Component
public class AdminAccountBootstrap implements ApplicationRunner {
    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final boolean enabled;
    private final String email;
    private final String password;
    private final String firstName;
    private final String lastName;
    private final String mobileNumber;

    public AdminAccountBootstrap(AppUserRepository users,
                                 PasswordEncoder passwordEncoder,
                                 @Value("${security.bootstrap-admin.enabled:false}") boolean enabled,
                                 @Value("${security.bootstrap-admin.email:}") String email,
                                 @Value("${security.bootstrap-admin.password:}") String password,
                                 @Value("${security.bootstrap-admin.first-name:}") String firstName,
                                 @Value("${security.bootstrap-admin.last-name:}") String lastName,
                                 @Value("${security.bootstrap-admin.mobile-number:}") String mobileNumber) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.enabled = enabled;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!enabled) {
            return;
        }
        if (email.isBlank() || password.length() < 8 || firstName.isBlank() || lastName.isBlank()
                || !mobileNumber.matches("\\d{10}")) {
            throw new IllegalStateException("Enabled admin bootstrap requires email, an 8+ character password, "
                    + "first name, last name, and a 10-digit mobile number");
        }

        String normalizedEmail = email.strip().toLowerCase(Locale.ROOT);
        if (users.existsByEmailIgnoreCase(normalizedEmail)) {
            return;
        }
        users.save(AppUser.bootstrapAdmin(firstName.strip(), lastName.strip(), normalizedEmail,
                passwordEncoder.encode(password), mobileNumber));
    }
}
