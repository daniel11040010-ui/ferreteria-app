package com.ferreteria.app_web_ferreteria.model;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;
    private String correo;
    private String contrasena;
    private String telefono;
    private String documentoIdentidad;
    private String direccion;

    @Column(nullable = false, updatable = false)
    private Timestamp fechaRegistro;

    @Column(columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean estaActivo;
} 