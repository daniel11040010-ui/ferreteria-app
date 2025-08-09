package com.ferreteria.app_web_ferreteria.security.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserMain implements UserDetails {

    private String name;
    private String lastname;
    private  String username;
    private String email;
    private  String password;
    private  String document;
    private Collection<? extends GrantedAuthority> authorities ;

    public UserMain(String name, String lastname,  String email, String password,
                    String username, Collection<? extends GrantedAuthority> authorities, String document) {
        this.name = name;
        this.authorities = authorities;
        this.password = password;
        this.email = email;
        this.username = username;
        this.lastname = lastname;
        this.document = document;
    }

    public  static UserMain build(User user){
        List<GrantedAuthority> authorities =
                user.getRoles().stream().map(
                        rol -> new SimpleGrantedAuthority(rol.getRolName().name())).collect(Collectors.toList());
        return  new UserMain(user.getName(), user.getLastname(), user.getEmail(), user.getPassword(), user.getUsername(), authorities, user.getDocument());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getLastname() {
        return lastname;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getDocument() {
        return document;
    }

}
