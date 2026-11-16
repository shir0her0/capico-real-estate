package com.capico.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "app_users")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;

    @Column(nullable = false, length = 80)
    private String firstName;

    @Column(nullable = false, length = 80)
    private String lastName;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 10)
    private String mobileNumber;

    @Column(length = 40)
    private String gender;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(length = 100)
    private String country;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Role role = Role.USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(nullable = false, updatable = false)
    private Instant createdDate;

    protected AppUser() {
    }

    public AppUser(String firstName, String lastName, String email, String passwordHash,
                   String mobileNumber, String gender, String city, String state, String country) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.mobileNumber = mobileNumber;
        this.gender = gender;
        this.city = city;
        this.state = state;
        this.country = country;
        this.role = Role.USER;
        this.status = AccountStatus.ACTIVE;
    }

    public static AppUser bootstrapAdmin(String firstName, String lastName, String email,
                                         String passwordHash, String mobileNumber) {
        AppUser admin = new AppUser(firstName, lastName, email, passwordHash,
                mobileNumber, null, null, null, null);
        admin.role = Role.ADMIN;
        return admin;
    }

    @PrePersist
    void setCreatedDate() {
        if (createdDate == null) {
            createdDate = Instant.now();
        }
    }

    public UUID getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getMobileNumber() { return mobileNumber; }
    public String getGender() { return gender; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getCountry() { return country; }
    public Role getRole() { return role; }
    public AccountStatus getStatus() { return status; }
    public Instant getCreatedDate() { return createdDate; }
}
