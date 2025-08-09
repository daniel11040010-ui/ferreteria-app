package com.ferreteria.app_web_ferreteria.controllers;

import com.ferreteria.app_web_ferreteria.model.Pedido;
import com.ferreteria.app_web_ferreteria.model.Cliente;
import com.ferreteria.app_web_ferreteria.security.entity.User;
import com.ferreteria.app_web_ferreteria.services.PedidoService;
import com.ferreteria.app_web_ferreteria.util.PedidoRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    public ResponseEntity<Page<Pedido>> getAllPedidos(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Pedido> pedidos = pedidoService.findAllPedidos(pageable);
        if (pedidos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(pedidos, HttpStatus.OK);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Pedido>> getPedidoById(@PathVariable Long id) {
        Optional<Pedido> pedido = pedidoService.findPedidoById(id);
        return pedido.map(value -> new ResponseEntity<>(pedido, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Pedido> createPedido(@RequestBody PedidoRequest pedidoRequest) {
        Pedido pedido = pedidoService.savePedidoConDetalles(pedidoRequest.getPedido(), pedidoRequest.getDetalles());
        return new ResponseEntity<>(pedido, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedido(@PathVariable Long id) {
        pedidoService.deletePedido(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<Page<Pedido>> getPedidosByCliente(@PathVariable Long clienteId,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        Page<Pedido> pedidos = pedidoService.findByCliente(cliente, pageable);
        if (pedidos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(pedidos, HttpStatus.OK);
        }
    }

    @GetMapping("/vendedor/{vendedorId}")
    public ResponseEntity<Page<Pedido>> getPedidosByVendedor(
        @PathVariable Long vendedorId,
        @RequestParam(required = false) String estado,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Pedido> pedidos;

        if (estado != null && !estado.isEmpty()) {
            pedidos = pedidoService.findByVendedorAndEstado(vendedorId, estado, pageable);
        } else {
            User vendedor = new User();
            vendedor.setId(vendedorId);
            pedidos = pedidoService.findByVendedor(vendedor, pageable);
        }

        if (pedidos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(pedidos, HttpStatus.OK);
        }
    }

    @GetMapping("/fecha")
    public ResponseEntity<Page<Pedido>> getPedidosByFecha(@RequestParam String inicio,
                                                         @RequestParam String fin,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Timestamp fechaInicio = Timestamp.valueOf(inicio);
        Timestamp fechaFin = Timestamp.valueOf(fin);
        Page<Pedido> pedidos = pedidoService.findByFechaPedidoBetween(fechaInicio, fechaFin, pageable);
        if (pedidos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(pedidos, HttpStatus.OK);
        }
    }

    @GetMapping("/cliente-vendedor")
    public ResponseEntity<Page<Pedido>> getPedidosByClienteAndVendedor(@RequestParam Long clienteId,
                                                                      @RequestParam Long vendedorId,
                                                                      @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        User vendedor = new User();
        vendedor.setId(vendedorId);
        Page<Pedido> pedidos = pedidoService.findByClienteAndVendedor(cliente, vendedor, pageable);
        if (pedidos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(pedidos, HttpStatus.OK);
        }
    }

    @GetMapping("/cliente-fecha")
    public ResponseEntity<Page<Pedido>> getPedidosByClienteAndFecha(@RequestParam Long clienteId,
                                                                   @RequestParam String inicio,
                                                                   @RequestParam String fin,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        Timestamp fechaInicio = Timestamp.valueOf(inicio);
        Timestamp fechaFin = Timestamp.valueOf(fin);
        Page<Pedido> pedidos = pedidoService.findByClienteAndFechaPedidoBetween(cliente, fechaInicio, fechaFin, pageable);
        if (pedidos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(pedidos, HttpStatus.OK);
        }
    }

    @GetMapping("/vendedor-fecha")
    public ResponseEntity<Page<Pedido>> getPedidosByVendedorAndFecha(@RequestParam Long vendedorId,
                                                                    @RequestParam String inicio,
                                                                    @RequestParam String fin,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        User vendedor = new User();
        vendedor.setId(vendedorId);
        Timestamp fechaInicio = Timestamp.valueOf(inicio);
        Timestamp fechaFin = Timestamp.valueOf(fin);
        Page<Pedido> pedidos = pedidoService.findByVendedorAndFechaPedidoBetween(vendedor, fechaInicio, fechaFin, pageable);
        if (pedidos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(pedidos, HttpStatus.OK);
        }
    }

    @GetMapping("/cliente-vendedor-fecha")
    public ResponseEntity<Page<Pedido>> getPedidosByClienteVendedorFecha(@RequestParam Long clienteId,
                                                                        @RequestParam Long vendedorId,
                                                                        @RequestParam String inicio,
                                                                        @RequestParam String fin,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        User vendedor = new User();
        vendedor.setId(vendedorId);
        Timestamp fechaInicio = Timestamp.valueOf(inicio);
        Timestamp fechaFin = Timestamp.valueOf(fin);
        Page<Pedido> pedidos = pedidoService.findByClienteAndVendedorAndFechaPedidoBetween(cliente, vendedor, fechaInicio, fechaFin, pageable);
        if (pedidos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(pedidos, HttpStatus.OK);
        }
    }

    @GetMapping("/estado")
    public ResponseEntity<Page<Pedido>> getPedidosByEstado(@RequestParam(required = false) String estado,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Pedido> pedidos = pedidoService.findByEstado(estado, pageable);
        if (pedidos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(pedidos, HttpStatus.OK);
        }
    }

    @PostMapping("/{id}/cambiar-estado")
    public ResponseEntity<?> cambiarEstadoPedido(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String nuevoEstado = body.get("estado");
        try {
            pedidoService.cambiarEstadoPedido(id, nuevoEstado);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/filtro")
    public ResponseEntity<Page<Pedido>> filtrarPedidos(
        @RequestParam(required = false) String estado,
        @RequestParam(required = false) Long vendedorId,
        @RequestParam(required = false) String inicio,
        @RequestParam(required = false) String fin,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Timestamp fechaInicio = (inicio != null && !inicio.isEmpty()) ? Timestamp.valueOf(inicio) : null;
        Timestamp fechaFin = (fin != null && !fin.isEmpty()) ? Timestamp.valueOf(fin) : null;
        Page<Pedido> pedidos = pedidoService.filtrarPedidos(estado, vendedorId, fechaInicio, fechaFin, pageable);
        if (pedidos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(pedidos, HttpStatus.OK);
        }
    }

    @GetMapping("/export")
    public ResponseEntity<List<Pedido>> exportarPedidos(
        @RequestParam(required = false) String estado,
        @RequestParam(required = false) Long vendedorId,
        @RequestParam(required = false) String inicio,
        @RequestParam(required = false) String fin
    ) {
        Timestamp fechaInicio = (inicio != null && !inicio.isEmpty()) ? Timestamp.valueOf(inicio) : null;
        Timestamp fechaFin = (fin != null && !fin.isEmpty()) ? Timestamp.valueOf(fin) : null;
        
        // Retorna List<Pedido> en lugar de Page<Pedido>
        List<Pedido> pedidos = pedidoService.exportarPedidos(estado, vendedorId, fechaInicio, fechaFin);
        
        // Siempre retorna OK, incluso si la lista está vacía
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }
} 