package com.Ayan.Mondal.VOTEONN.MODEL;

import jakarta.persistence.*;

@Entity
@Table(name = "user_entity")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String phone;
    private String gender;
    private String password;
    private String role;
    private int age;

    // ── OAuth2 fields ───────────────────────────────────────────
    // Stores Google's unique user ID ("sub" from the ID token)
    @Column(name = "google_id")
    private String googleId;

    // "LOCAL" for normal email/password users, "GOOGLE" for OAuth2 users
    @Column(name = "auth_provider")
    private String authProvider = "LOCAL";
    // ────────────────────────────────────────────────────────────

    public UserEntity() {}

    // ── Getters & Setters ───────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGoogleId() { return googleId; }
    public void setGoogleId(String googleId) { this.googleId = googleId; }

    public String getAuthProvider() { return authProvider; }
    public void setAuthProvider(String authProvider) { this.authProvider = authProvider; }
}