package com.capico.auth.dto;

import com.capico.auth.entity.AccountStatus;
import com.capico.auth.entity.AppUser;
import com.capico.auth.entity.Role;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID userId,
        String firstName,
        String lastName,
        String email,
        String mobileNumber,
        String gender,
        String city,
        String state,
        String country,
        Role role,
        AccountStatus status,
        Instant createdDate
) {
    public static UserResponse from(AppUser user) {
        return new UserResponse(
                user.getUserId(), user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getMobileNumber(), user.getGender(), user.getCity(), user.getState(),
                user.getCountry(), user.getRole(), user.getStatus(), user.getCreatedDate()
        );
    }
}
