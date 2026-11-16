package com.capico.auth.service;

import com.capico.auth.dto.LoginRequest;
import com.capico.auth.dto.LoginResponse;
import com.capico.auth.dto.RegisterRequest;
import com.capico.auth.dto.UserResponse;
import com.capico.auth.entity.AccountStatus;
import com.capico.auth.entity.AppUser;
import com.capico.auth.exception.DuplicateEmailException;
import com.capico.auth.exception.InvalidCredentialsException;
import com.capico.auth.repository.AppUserRepository;
import com.capico.auth.repository.RevokedTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class AuthService {
    private final AppUserRepository users;
    private final RevokedTokenRepository revokedTokens;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final long expirationMinutes;

    public AuthService(AppUserRepository users,
                       RevokedTokenRepository revokedTokens,
                       PasswordEncoder passwordEncoder,
                       JwtEncoder jwtEncoder,
                       @Value("${security.jwt.issuer}") String issuer,
                       @Value("${security.jwt.expiration-minutes}") long expirationMinutes) {
        this.users = users;
        this.revokedTokens = revokedTokens;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.expirationMinutes = expirationMinutes;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (users.existsByEmailIgnoreCase(email)) {
            throw new DuplicateEmailException();
        }

        AppUser user = new AppUser(
                request.firstName().strip(), request.lastName().strip(), email,
                passwordEncoder.encode(request.password()), request.mobileNumber(),
                blankToNull(request.gender()), blankToNull(request.city()),
                blankToNull(request.state()), blankToNull(request.country())
        );
        return UserResponse.from(users.save(user));
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        AppUser user = users.findByEmailIgnoreCase(normalizeEmail(request.email()))
                .filter(candidate -> passwordEncoder.matches(request.password(), candidate.getPasswordHash()))
                .filter(candidate -> candidate.getStatus() == AccountStatus.ACTIVE)
                .orElseThrow(InvalidCredentialsException::new);

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(expirationMinutes * 60);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(user.getEmail())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .id(UUID.randomUUID().toString())
                .claim("roles", List.of(user.getRole().name()))
                .claim("userId", user.getUserId().toString())
                .build();

        return new LoginResponse(jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue(),
                "Bearer", expirationMinutes * 60);
    }

    @Transactional
    public void logout(Jwt jwt) {
        String tokenId = jwt.getId();
        if (tokenId != null && !revokedTokens.existsById(tokenId)) {
            revokedTokens.save(new com.capico.auth.entity.RevokedToken(tokenId, jwt.getExpiresAt()));
        }
    }

    @Transactional(readOnly = true)
    public UserResponse currentUser(Jwt jwt) {
        AppUser user = users.findByEmailIgnoreCase(jwt.getSubject())
                .orElseThrow(InvalidCredentialsException::new);
        return UserResponse.from(user);
    }

    private String normalizeEmail(String email) {
        return email.strip().toLowerCase(Locale.ROOT);
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.strip();
    }
}
