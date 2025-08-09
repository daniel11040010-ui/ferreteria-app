package com.ferreteria.app_web_ferreteria.recoverypassword.controller;

import com.ferreteria.app_web_ferreteria.recoverypassword.dto.ChangePasswordDTO;
import com.ferreteria.app_web_ferreteria.recoverypassword.service.EmailService;
import com.ferreteria.app_web_ferreteria.security.dto.ApiResponse;
import com.ferreteria.app_web_ferreteria.security.dto.ApiResponsePassWord;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email-password")
@CrossOrigin(origins = "*")
public class EmailController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailController.class);

    @Autowired
    EmailService emailService;

    @GetMapping("/send-email/{correo}/{username}")
    public ResponseEntity<ApiResponse> sendEmailTemplate(@PathVariable("correo") String correo, @PathVariable("username") String username) {
        LOGGER.info("Enviando correo electrónico con plantilla");
        return ResponseEntity.ok(emailService.sendEmailTemplate(correo, username));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponsePassWord> changePassword(@Valid @RequestBody ChangePasswordDTO dto) {
        LOGGER.info("Cambiando contraseña");
        return ResponseEntity.ok(emailService.changePassword(dto));
    }
}
