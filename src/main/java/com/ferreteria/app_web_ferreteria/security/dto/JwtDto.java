package com.ferreteria.app_web_ferreteria.security.dto;

import com.ferreteria.app_web_ferreteria.security.enums.RolNombre;

import java.util.List;

public class JwtDto {

    private String token;
    private List<RolNombre> roles;
    private String nombreApellido;
    private Long id;

    public JwtDto(String token) {
        this.token = token;
    }


    public JwtDto(String token, List<RolNombre> roles, String nombreApellido, Long id) {
        this.token = token;
        this.roles = roles;
        this.nombreApellido = nombreApellido;
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<RolNombre> getRoles() {
        return roles;
    }

    public void setRoles(List<RolNombre> roles) {
        this.roles = roles;
    }

    public String getNombreApellido() {
        return nombreApellido;
    }

    public void setNombreApellido(String nombreApellido) {
        this.nombreApellido = nombreApellido;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }


}
