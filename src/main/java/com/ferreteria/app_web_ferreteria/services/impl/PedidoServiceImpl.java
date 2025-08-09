package com.ferreteria.app_web_ferreteria.services.impl;

import com.ferreteria.app_web_ferreteria.model.Pedido;
import com.ferreteria.app_web_ferreteria.model.PedidoDetalle;
import com.ferreteria.app_web_ferreteria.model.Cliente;
import com.ferreteria.app_web_ferreteria.repository.PedidoRepository;
import com.ferreteria.app_web_ferreteria.repository.PedidoDetalleRepositoy;
import com.ferreteria.app_web_ferreteria.security.entity.User;
import com.ferreteria.app_web_ferreteria.security.repository.UserRepository;
import com.ferreteria.app_web_ferreteria.services.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Random;
import org.springframework.transaction.annotation.Transactional;
import com.ferreteria.app_web_ferreteria.util.EstadoPedido;

@Service
public class PedidoServiceImpl  implements PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PedidoDetalleRepositoy   pedidoDetalleRepository;


    @Override
    public Page<Pedido> findAllPedidos(Pageable pageable) {
        return pedidoRepository.findAll(pageable);
    }

    @Override
    public Optional<Pedido> findPedidoById(Long id) {
        return pedidoRepository.findById(id);
    }

    @Override
    public Pedido savePedidoConDetalles(Pedido pedido, List<PedidoDetalle> detalles) {
        // 1. Obtener todos los vendedores activos
        List<User> vendedores = userRepository.findActiveVendedores();
        if (vendedores.isEmpty()) throw new RuntimeException("No hay vendedores activos");

        // 2. Buscar el vendedor con menos pedidos hoy
        java.sql.Date hoy = new java.sql.Date(System.currentTimeMillis());
        List<User> candidatos = new ArrayList<>();
        long minPedidos = Long.MAX_VALUE;

        for (User vendedor : vendedores) {
            long pedidosHoy = pedidoRepository.countPedidosByVendedorAndFecha(vendedor.getId(), hoy);
            if (pedidosHoy < minPedidos) {
                minPedidos = pedidosHoy;
                candidatos.clear();
                candidatos.add(vendedor);
            } else if (pedidosHoy == minPedidos) {
                candidatos.add(vendedor);
            }
        }

        // Si hay varios con el mismo mínimo, elige uno al azar
        User vendedorAsignado;
        if (candidatos.size() == 1) {
            vendedorAsignado = candidatos.get(0);
        } else {
            Collections.shuffle(candidatos);
            vendedorAsignado = candidatos.get(0);
        }

        // 3. Asignar el vendedor al pedido
        pedido.setVendedor(vendedorAsignado);

        // 4. Guardar el pedido
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // 5. Guardar detalles
        for (PedidoDetalle detalle : detalles) {
            detalle.setPedido(pedidoGuardado);
            pedidoDetalleRepository.save(detalle);
        }
        return pedidoGuardado;
    }

    @Override
    public Pedido updatePedido(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    @Override
    public void deletePedido(Long id) {
        pedidoRepository.deleteById(id);
    }

    @Override
    public Page<Pedido> findByCliente(Cliente cliente, Pageable pageable) {
        return pedidoRepository.findByClienteOrderByIdDesc(cliente, pageable);
    }

    @Override
    public Page<Pedido> findByVendedor(User vendedor, Pageable pageable) {
        return pedidoRepository.findByVendedor(vendedor, pageable);
    }

    @Override
    public Page<Pedido> findByFechaPedidoBetween(Timestamp fechaInicio, Timestamp fechaFin, Pageable pageable) {
        return pedidoRepository.findByFechaPedidoBetween(fechaInicio, fechaFin, pageable);
    }

    @Override
    public Page<Pedido> findByClienteAndVendedor(Cliente cliente, User vendedor, Pageable pageable) {
        return pedidoRepository.findByClienteAndVendedor(cliente, vendedor, pageable);
    }

    @Override
    public Page<Pedido> findByClienteAndFechaPedidoBetween(Cliente cliente, Timestamp fechaInicio, Timestamp fechaFin, Pageable pageable) {
        return pedidoRepository.findByClienteAndFechaPedidoBetween(cliente, fechaInicio, fechaFin, pageable);
    }

    @Override
    public Page<Pedido> findByVendedorAndFechaPedidoBetween(User vendedor, Timestamp fechaInicio, Timestamp fechaFin, Pageable pageable) {
        return pedidoRepository.findByVendedorAndFechaPedidoBetween(vendedor, fechaInicio, fechaFin, pageable);
    }

    @Override
    public Page<Pedido> findByClienteAndVendedorAndFechaPedidoBetween(Cliente cliente, User vendedor, Timestamp fechaInicio, Timestamp fechaFin, Pageable pageable) {
        return pedidoRepository.findByClienteAndVendedorAndFechaPedidoBetween(cliente, vendedor, fechaInicio, fechaFin, pageable);
    }

    @Override
    public Page<Pedido> findByEstado(String estado, Pageable pageable) {
        return pedidoRepository.findByEstado(estado, pageable);
    }

    @Override
    public Page<Pedido> findByVendedorAndEstado(Long vendedorId, String estado, Pageable pageable) {
        EstadoPedido estadoEnum = EstadoPedido.valueOf(estado);
        return pedidoRepository.findByVendedor_IdAndEstado(vendedorId, estadoEnum, pageable);
    }

    @Override
    @Transactional
    public void cambiarEstadoPedido(Long pedidoId, String nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        pedido.setEstado(EstadoPedido.valueOf(nuevoEstado));
        pedidoRepository.save(pedido);
    }

    @Override
    public Page<Pedido> filtrarPedidos(String estado, Long vendedorId, Timestamp fechaInicio, Timestamp fechaFin, Pageable pageable) {
        com.ferreteria.app_web_ferreteria.util.EstadoPedido estadoEnum = null;
        if (estado != null && !estado.isEmpty()) {
            try {
                estadoEnum = com.ferreteria.app_web_ferreteria.util.EstadoPedido.valueOf(estado);
            } catch (IllegalArgumentException e) {
                // Si el estado no es válido, puedes lanzar una excepción o devolver vacío
                return Page.empty(pageable);
            }
        }
        return pedidoRepository.filtrarPedidos(
            estadoEnum,
            vendedorId,
            fechaInicio,
            fechaFin,
            pageable
        );
    }

    @Override
    public List<Pedido> exportarPedidos(String estado, Long vendedorId, Timestamp fechaInicio, Timestamp fechaFin) {
        com.ferreteria.app_web_ferreteria.util.EstadoPedido estadoEnum = null;
        if (estado != null && !estado.isEmpty()) {
            try {
                estadoEnum = com.ferreteria.app_web_ferreteria.util.EstadoPedido.valueOf(estado);
            } catch (IllegalArgumentException e) {
                // Si el estado no es válido, retorna lista vacía
                return new ArrayList<>();
            }
        }
        return pedidoRepository.exportarPedidos(
            estadoEnum,
            vendedorId,
            fechaInicio,
            fechaFin
        );
    }

} 