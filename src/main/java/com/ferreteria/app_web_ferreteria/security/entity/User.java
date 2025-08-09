package com.ferreteria.app_web_ferreteria.security.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String document;

    @NotNull
    private String lastname;

    @NotNull
    @Column(unique = true)
    private String username;

    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    private boolean isActive;

    private String tokenPassword;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_rol",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<Rol> roles = new HashSet<>();

    public User(String name, String lastname, String username, String email, String password, boolean isActive,
        String document
    ) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.isActive = isActive;
        this.lastname = lastname;
        this.document = document;
    }


}


