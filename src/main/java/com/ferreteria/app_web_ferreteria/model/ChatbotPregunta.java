package com.ferreteria.app_web_ferreteria.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatbotPregunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String pregunta;

    @Column(columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean estaActivo;
} 