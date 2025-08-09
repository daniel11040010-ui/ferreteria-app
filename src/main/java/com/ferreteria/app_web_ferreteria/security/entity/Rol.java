package com.ferreteria.app_web_ferreteria.security.entity;

import com.ferreteria.app_web_ferreteria.security.enums.RolNombre;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RolNombre rolName;
}
