package com.ferreteria.app_web_ferreteria.controllers;

import com.ferreteria.app_web_ferreteria.model.TipoPintura;
import com.ferreteria.app_web_ferreteria.services.TipoPinturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tipos-pintura")
@CrossOrigin(origins = "*")
public class TipoPinturaController {

    @Autowired
    private TipoPinturaService tipoPinturaService;

    @GetMapping
    public ResponseEntity<List<TipoPintura>> getAllTiposPintura() {
        List<TipoPintura> tipos = tipoPinturaService.findAll();
        return ResponseEntity.ok(tipos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoPintura> getTipoPinturaById(@PathVariable Long id) {
        Optional<TipoPintura> tipo = tipoPinturaService.findById(id);
        return tipo.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
