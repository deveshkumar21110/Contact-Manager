package com.example.scm.entities;

import java.util.ArrayList;
import java.util.List;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "user")
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
// @Builder
public class User {

    @Id
    @Column(name = "user_id")
    private String id;
    @Column(name = "user_name", nullable = false)
    private String name;
    @Column(name = "user_email", nullable = false, unique = false)
    private String email;
    private String password;
    // @Column(name = "user_role", nullable = false, columnDefinition = "varchar(255) default 'USER'")
    private String about;
    private String profilePic;
    private String phoneNumber;

    private boolean enabled = true;
    private boolean emailVerified = false;
    private boolean phoneVerified = false;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roleList = new ArrayList<>();

    // login provider
    @Enumerated(value = EnumType.STRING)
    private Providers provider = Providers.SELF;
    private String providerId;

    // One-to-many mapping
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Contact> contacts = new ArrayList<>();
}
