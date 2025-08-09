package com.ferreteria.app_web_ferreteria.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    @ManyToOne
    @JoinColumn(name = "marca_id", nullable = false)
    private Marca marca;

    @ManyToOne
    @JoinColumn(name = "tipo_pintura_id", nullable = false)
    private TipoPintura tipoPintura;

    private String nombre;
    private String color;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

	@Column(columnDefinition = "TEXT")
    private String foto; // URL de la imagen


    private Double precioCompra;  // obligatorio
    private Double precioVentaGalon; // obligatorio
    private Boolean permiteGranel; // obligatorio
    private Double precioMedioGalon; // 0.0
    private Double precioCuartoGalon; // 0.0
    private Double precioOctavoGalon; // 0.0
    private Double precioDieciseisavoGalon; // 0.0
    private Double precioTreintaidosavoGalon; // 0.0
    private Integer stockTotal; // 100
    private Integer stockMinimo; // 10
    private Integer cantidadCerrados; // 0
    private Integer cantidadAbiertos; // 0
    private String estante; // ""
    private String fila; // ""
    private String area; // ""

    @Column(columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean estaActivo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_creacion", updatable = false)
    private java.util.Date fechaCreacion;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_actualizacion")
    private java.util.Date fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = new java.util.Date();
        fechaActualizacion = fechaCreacion;
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = new java.util.Date();
    }
} 