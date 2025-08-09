package com.ferreteria.app_web_ferreteria.services;

import com.ferreteria.app_web_ferreteria.model.Pedido;
import com.ferreteria.app_web_ferreteria.model.PedidoDetalle;
import com.ferreteria.app_web_ferreteria.model.Cliente;
import com.ferreteria.app_web_ferreteria.security.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface PedidoService {
    Page<Pedido> findAllPedidos(Pageable pageable);
    Optional<Pedido> findPedidoById(Long id);
    //Pedido savePedido(Pedido pedido);
    Pedido updatePedido(Pedido pedido);
    void deletePedido(Long id);
    Page<Pedido> findByCliente(Cliente cliente, Pageable pageable);
    Page<Pedido> findByVendedor(User vendedor, Pageable pageable);
    Page<Pedido> findByFechaPedidoBetween(Timestamp fechaInicio, Timestamp fechaFin, Pageable pageable);
    Page<Pedido> findByClienteAndVendedor(Cliente cliente, User vendedor, Pageable pageable);
    Page<Pedido> findByClienteAndFechaPedidoBetween(Cliente cliente, Timestamp fechaInicio, Timestamp fechaFin, Pageable pageable);
    Page<Pedido> findByVendedorAndFechaPedidoBetween(User vendedor, Timestamp fechaInicio, Timestamp fechaFin, Pageable pageable);
    Page<Pedido> findByClienteAndVendedorAndFechaPedidoBetween(Cliente cliente, User vendedor, Timestamp fechaInicio, Timestamp fechaFin, Pageable pageable);
    Page<Pedido> findByEstado(String estado, Pageable pageable);
    Pedido savePedidoConDetalles(Pedido pedido, List<PedidoDetalle> detalles);
    Page<Pedido> findByVendedorAndEstado(Long vendedorId, String estado, Pageable pageable);
    void cambiarEstadoPedido(Long pedidoId, String nuevoEstado);
    Page<Pedido> filtrarPedidos(String estado, Long vendedorId, Timestamp fechaInicio, Timestamp fechaFin, Pageable pageable);
    List<Pedido> exportarPedidos(String estado, Long vendedorId, Timestamp fechaInicio, Timestamp fechaFin);
    
} 