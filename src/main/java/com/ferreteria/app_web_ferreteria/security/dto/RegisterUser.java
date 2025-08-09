package com.ferreteria.app_web_ferreteria.security.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;

public class RegisterUser {


    private Long id;

    @NotBlank(message = "name required")
    private String name;

    @NotBlank(message = "lastname required")
    private String lastname;

    @NotBlank(message = "username required")
    @Column(unique = true)
    private  String username;

    @NotBlank(message = "email required")
    private String email;

    @NotBlank(message = "document required")
    private String document;

    @NotBlank
    private  String password;

    private boolean isActive;

    private Set<String> roles = new HashSet<>();

    public RegisterUser() {
    }

    public RegisterUser(Long id, String name, String lastname, String username, String email, String password,
                        boolean isActive, Set<String> roles, String document) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.username = username;
        this.email = email;
        this.password = password;
        this.isActive = isActive;
        this.roles = roles;
        this.document = document;
    }

    public RegisterUser(String name, String lastname, String username, String email, String password, boolean isActive,
                        Set<String> roles, String document) {
        this.name = name;
        this.lastname = lastname;
        this.username = username;
        this.email = email;
        this.password = password;
        this.isActive = isActive;
        this.roles = roles;
        this.document = document;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public @NotBlank(message = "document required") String getDocument() {
        return document;
    }

    public void setDocument(@NotBlank(message = "document required") String document) {
        this.document = document;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("RegisterUser{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", lastname='").append(lastname).append('\'');
        sb.append(", username='").append(username).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", document='").append(document).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", isActive='").append(isActive).append('\'');
        sb.append(", roles=").append(roles);
        sb.append('}');
        return sb.toString();
    }
}
