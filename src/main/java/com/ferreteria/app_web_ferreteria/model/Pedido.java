package com.ferreteria.app_web_ferreteria.model;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;

import com.ferreteria.app_web_ferreteria.security.entity.User;
import com.ferreteria.app_web_ferreteria.util.EstadoPedido;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "vendedor_id")
    private User vendedor;


    private Timestamp fechaPedido;
    private Timestamp fechaActualizacion;
    private Double subtotal;
    private Double igv;
    private Double total;

    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

} 