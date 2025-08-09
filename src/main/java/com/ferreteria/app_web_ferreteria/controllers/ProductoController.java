package com.ferreteria.app_web_ferreteria.controllers;

import com.ferreteria.app_web_ferreteria.model.Producto;
import com.ferreteria.app_web_ferreteria.services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping("/list")
    public ResponseEntity<List<Producto>> getAllProductos() {
        List<Producto> productos = productoService.findAllProductos();
        if (productos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(productos, HttpStatus.OK);
        }
    }

    @GetMapping
    public ResponseEntity<Page<Producto>> getAllProductosPaginados(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Producto> productos = productoService.findAllProductos(pageable);
        if (productos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(productos, HttpStatus.OK);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Producto>> getProductoById(@PathVariable Long id) {
        Optional<Producto> producto = productoService.findProductoById(id);
        return producto.map(value -> new ResponseEntity<>(producto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Producto> createOrUpdateProducto(@RequestBody Producto producto) {
        Producto productoRetorno;
        if (producto.getId() != null) {
            productoRetorno = productoService.updateProducto(producto);
        } else {
            productoRetorno = productoService.saveProducto(producto);
        }
        return new ResponseEntity<>(productoRetorno, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
        productoService.deleteProducto(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/filtro")
    public ResponseEntity<Page<Producto>> getProductoFiltro(@RequestParam String textoBuscar,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Producto> productos = productoService.filtrarProducto(textoBuscar, pageable);
        if (productos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(productos, HttpStatus.OK);
        }
    }

    @GetMapping("/estado")
    public ResponseEntity<Page<Producto>> getProductosEstado(@RequestParam Boolean estado,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Producto> productos = productoService.listarProductosEstado(estado, pageable);
        if (productos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(productos, HttpStatus.OK);
        }
    }

    @GetMapping("/filtro-avanzado")
    public ResponseEntity<Page<Producto>> filtrarProductosAvanzado(
            @RequestParam(required = false) String textoBuscar,
            @RequestParam(required = false) Long marcaId,
            @RequestParam(required = false) Long tipoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Producto> productos = productoService.filtrarProductosAvanzado(textoBuscar, marcaId, tipoId, pageable);
        
        if (productos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(productos, HttpStatus.OK);
        }
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstadoProducto(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        Boolean nuevoEstado = body.get("estaActivo");
        Optional<Producto> productoOpt = productoService.findProductoById(id);
        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            producto.setEstaActivo(nuevoEstado);
            productoService.saveProducto(producto);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/stock-minimo/count")
    public ResponseEntity<Long> getCountProductosStockMinimo() {
        Long count = productoService.countProductosStockMinimo();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("/stock-minimo")
    public ResponseEntity<Page<Producto>> getProductosStockMinimo(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Producto> productos = productoService.findProductosStockMinimo(pageable);
        if (productos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(productos, HttpStatus.OK);
        }
    }

    @GetMapping("/verificar-duplicado")
    public ResponseEntity<Boolean> verificarProductoDuplicado(
            @RequestParam String nombre,
            @RequestParam String color,
            @RequestParam Long marcaId,
            @RequestParam Long tipoId,
            @RequestParam(required = false) Long excludeId
    ) {
        boolean existe = productoService.existsByNombreAndColorAndMarcaAndTipo(nombre, color, marcaId, tipoId, excludeId);
        return new ResponseEntity<>(existe, HttpStatus.OK);
    }
} 