package com.capico.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(max = 80) String firstName,
        @NotBlank @Size(max = 80) String lastName,
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(min = 8, max = 72) String password,
        @NotBlank @Pattern(regexp = "\\d{10}", message = "Mobile number must contain exactly 10 digits") String mobileNumber,
        @Size(max = 40) String gender,
        @Size(max = 100) String city,
        @Size(max = 100) String state,
        @Size(max = 100) String country
) {
}
