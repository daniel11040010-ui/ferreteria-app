package com.ferreteria.app_web_ferreteria.security.controller;


import com.ferreteria.app_web_ferreteria.security.dto.ApiResponse;
import com.ferreteria.app_web_ferreteria.security.dto.JwtDto;
import com.ferreteria.app_web_ferreteria.security.dto.LoginUser;
import com.ferreteria.app_web_ferreteria.security.dto.RegisterUser;
import com.ferreteria.app_web_ferreteria.security.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    UserService userService;

    @Operation(summary = "Tambien se utiliza para actualizar(con el id) y crear(sin id)")
    @PostMapping("/nuevo")
    public ResponseEntity<ApiResponse> nuevo(@Valid @RequestBody RegisterUser registerUser){
        LOGGER.info("Creando un nuevo usuario: {}", registerUser.getRoles());
        return ResponseEntity.ok(userService.save(registerUser));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtDto> login(@Valid @RequestBody LoginUser loginUser){
        LOGGER.info("Iniciando sesión para el usuario: {}", loginUser.getUsername());
        return ResponseEntity.ok(userService.login(loginUser));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtDto> refresh(@RequestParam String token) throws ParseException {
        LOGGER.info("Refrescando token  {}", token);
        return ResponseEntity.ok(userService.refresh(token));
    }

}