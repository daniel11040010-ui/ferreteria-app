package com.ferreteria.app_web_ferreteria.security.controller;


import com.ferreteria.app_web_ferreteria.security.dto.ApiResponse;
import com.ferreteria.app_web_ferreteria.security.entity.Rol;
import com.ferreteria.app_web_ferreteria.security.entity.User;
import com.ferreteria.app_web_ferreteria.security.service.RolService;
import com.ferreteria.app_web_ferreteria.security.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/user")
public class UsuarioController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private RolService rolService;


    @GetMapping("/roles")
    public ResponseEntity<List<Rol>> listRoles(){
        List<Rol> roles = rolService.getRoles();
        LOGGER.info("Listando roles: {}", roles);
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    // get all users sin paginación
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getUsers();
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(users, HttpStatus.OK);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findByIdUser(@PathVariable("id") Long id){
        Optional<User> user = userService.getByIdUser(id);
        if(user.isPresent()){
            LOGGER.info("Usuario encontrado con ID {}: {}", id, user.get());
            return new ResponseEntity<>(user, HttpStatus.OK);
        }else{
            LOGGER.warn("Usuario no encontrado con ID {}", id);
            return new ResponseEntity<>(new ApiResponse("User not found"),  HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/validarUsername/{username}")
    public ResponseEntity<?> findByIdUserName(@PathVariable("username") String username){
        boolean existe = userService.existByUsername(username);
        return new ResponseEntity<>(existe,  HttpStatus.OK);
    }

    @PostMapping("/{id}/{newState}")
    public ResponseEntity<?> updateStateUser(@PathVariable("id") Long id, @PathVariable("newState") boolean newState){
        ApiResponse response = userService.updateState(id, newState);
        LOGGER.info("Estado de usuario actualizado con ID {}: Nuevo estado: {}", id, newState);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/validarcorreo")
    public ResponseEntity<?> buscarPorCorreoElectronico(@RequestParam String correoElectronico) {
        User user = userService.findByCorreoElectronico(correoElectronico);
        if (user != null) {
            if (user.isActive()) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Ese usuario no está activo");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Ese usuario no existe");
        }
    }


    @GetMapping
    public ResponseEntity<Page<User>> getAllUsuarios(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usuarios = userService.findAllUsuarioss(pageable);
        if (usuarios.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(usuarios, HttpStatus.OK);
        }
    }

    @GetMapping("/estado")
    public ResponseEntity<Page<User>> getUsuariosEstado(@RequestParam boolean estado ,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usuarios = userService.listarUsuariosEstado(estado, pageable);
        System.out.println(usuarios);
        if (usuarios.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(usuarios, HttpStatus.OK);
        }
    }

    @GetMapping("/filtro")
    public ResponseEntity<Page<User>> getUsuarioFiltro(@RequestParam String textoBuscar ,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usuarios = userService.filtrarUsuarios(textoBuscar, pageable);
        if (usuarios.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(usuarios, HttpStatus.OK);
        }
    }

    @GetMapping("/validardocumento")
    public ResponseEntity<?> validarDocumento(@RequestParam String documento) {
        boolean existe = userService.existByDocumento(documento);
        return new ResponseEntity<>(existe, HttpStatus.OK);
    }

    @GetMapping("/vendedores")
    public ResponseEntity<List<User>> getVendedores() {
        List<User> vendedores = userService.getActiveVendedores();
        if (vendedores.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(vendedores, HttpStatus.OK);
        }
    }

}
