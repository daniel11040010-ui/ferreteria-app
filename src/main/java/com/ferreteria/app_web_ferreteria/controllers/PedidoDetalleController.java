package com.ferreteria.app_web_ferreteria.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ferreteria.app_web_ferreteria.model.PedidoDetalle;
import com.ferreteria.app_web_ferreteria.repository.PedidoDetalleRepository;

import java.util.List;

@RestController
@RequestMapping("/api/pedido-detalle")
@CrossOrigin(origins = "*")
public class PedidoDetalleController {

    @Autowired
    private PedidoDetalleRepository pedidoDetalleRepository;

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<PedidoDetalle>> getDetallesByPedidoId(@PathVariable Long pedidoId) {
        List<PedidoDetalle> detalles = pedidoDetalleRepository.findByPedidoId(pedidoId);
        if (detalles.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(detalles, HttpStatus.OK);
        }
    }
}
