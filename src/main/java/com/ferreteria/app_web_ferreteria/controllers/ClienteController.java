package com.ferreteria.app_web_ferreteria.controllers;

import com.ferreteria.app_web_ferreteria.model.Cliente;
import com.ferreteria.app_web_ferreteria.services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/list")
    public ResponseEntity<List<Cliente>> getAllClientes() {
        List<Cliente> clientes = clienteService.findAllClientes();
        if (clientes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(clientes, HttpStatus.OK);
        }
    }

    @GetMapping
    public ResponseEntity<Page<Cliente>> getAllClientesPaginados(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Cliente> clientes = clienteService.findAllClientes(pageable);
        if (clientes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(clientes, HttpStatus.OK);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Cliente>> getClienteById(@PathVariable Long id) {
        Optional<Cliente> cliente = clienteService.findClienteById(id);
        return cliente.map(value -> new ResponseEntity<>(cliente, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Cliente> createOrUpdateCliente(@RequestBody Cliente cliente) {
        Cliente clienteRetorno;
        if (cliente.getId() != null) {
            clienteRetorno = clienteService.updateCliente(cliente);
        } else {
            cliente.setFechaRegistro(new java.sql.Timestamp(System.currentTimeMillis()));
            cliente.setEstaActivo(true);
            clienteRetorno = clienteService.saveCliente(cliente);
        }
        return new ResponseEntity<>(clienteRetorno, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        clienteService.deleteCliente(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/filtro")
    public ResponseEntity<Page<Cliente>> getClienteFiltro(@RequestParam String textoBuscar,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Cliente> clientes = clienteService.filtrarCliente(textoBuscar, pageable);
        if (clientes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(clientes, HttpStatus.OK);
        }
    }

    @GetMapping("/estado")
    public ResponseEntity<Page<Cliente>> getClientesEstado(@RequestParam Boolean estado,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Cliente> clientes = clienteService.listarClientesEstado(estado, pageable);
        if (clientes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(clientes, HttpStatus.OK);
        }
    }

    @GetMapping("/exists-correo")
    public ResponseEntity<Boolean> existsByCorreo(@RequestParam String correo) {
        boolean exists = clienteService.existsByCorreo(correo);
        HttpStatus status = exists ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(exists, status);
    }

    @GetMapping("/exists-correo-cliente")
    public ResponseEntity<Boolean> existsByCorreoUsuario(@RequestParam String correo) {
        boolean exists = clienteService.existsByCorreo(correo);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists-documento")
    public ResponseEntity<Boolean> existsByDocumentoIdentidad(@RequestParam String documentoIdentidad) {
        boolean exists = clienteService.existsByDocumentoIdentidad(documentoIdentidad);
        HttpStatus status = exists ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(exists, status);
    }

    @GetMapping("/existsByNombreApellido")
    public boolean existsByNombreApellido(@RequestParam String nombre, @RequestParam String apellido) {
        return clienteService.existsByNombreAndApellido(nombre, apellido);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginCliente(@RequestBody Map<String, String> loginData) {
        String correo = loginData.get("username");
        String contrasena = loginData.get("password");
        System.out.println("Contraseña recibida: " + contrasena);
        Optional<Cliente> clienteOpt = clienteService.findAllClientes().stream()
            .filter(c -> c.getCorreo().equalsIgnoreCase(correo))
            .findFirst();
        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            System.out.println("Contraseña en BD: " + cliente.getContrasena());
            System.out.println("Match: " + passwordEncoder.matches(contrasena, cliente.getContrasena()));
            if (passwordEncoder.matches(contrasena, cliente.getContrasena())) {
                // Aquí deberías generar un JWT real, pero para ejemplo:
                Map<String, Object> resp = new HashMap<>();
                resp.put("token", "dummy-token");
                resp.put("roles", List.of("CLIENTE"));
                resp.put("nombreApellido", cliente.getNombre() + " " + cliente.getApellido());
                resp.put("id", cliente.getId());
                return ResponseEntity.ok(resp);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
    }
} 