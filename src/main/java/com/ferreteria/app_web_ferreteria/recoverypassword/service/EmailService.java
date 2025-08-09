package com.ferreteria.app_web_ferreteria.recoverypassword.service;


import com.ferreteria.app_web_ferreteria.model.Cliente;
import com.ferreteria.app_web_ferreteria.recoverypassword.dto.ChangePasswordDTO;
import com.ferreteria.app_web_ferreteria.recoverypassword.dto.EmailValuesDTO;
import com.ferreteria.app_web_ferreteria.repository.ClienteRepository;
import com.ferreteria.app_web_ferreteria.security.dto.ApiResponse;
import com.ferreteria.app_web_ferreteria.security.dto.ApiResponsePassWord;
import com.ferreteria.app_web_ferreteria.security.entity.User;
import com.ferreteria.app_web_ferreteria.security.exceptions.CustomException;
import com.ferreteria.app_web_ferreteria.security.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    TemplateEngine templateEngine;

    @Value("${mail.urlFront}")
    private String urlFront;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ClienteRepository clienteRepository; // ⭐ AGREGAR ESTO

    @Autowired
    PasswordEncoder passwordEncoder;

    @Value("${spring.mail.username}")
    private String mailFrom;

    private static final String subject = "Cambio de Contraseña";

    public ApiResponse sendEmailTemplate(String correo, String username) {
        LOGGER.info("Enviando correo electrónico con plantilla para: {}", correo);

        // ⭐ PRIMERO BUSCAR EN CLIENTES
        Optional<Cliente> clienteOpt = clienteRepository.findByCorreo(correo);
        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            if (!cliente.getEstaActivo()) {
                throw new CustomException(HttpStatus.NOT_FOUND, "Ese cliente no está activo");
            }
            return sendEmailToCliente(cliente);
        }

        // ⭐ SI NO ENCUENTRA EN CLIENTES, BUSCAR EN USUARIOS
        Optional<User> usuarioOpt = userRepository.findByUsernameOrEmail(username, correo);
        if (!usuarioOpt.isPresent()) {
            LOGGER.warn("No se encontró usuario ni cliente con correo {}", correo);
            throw new CustomException(HttpStatus.NOT_FOUND, "Ese usuario no existe");
        } else if (!usuarioOpt.get().isActive()) {
            throw new CustomException(HttpStatus.NOT_FOUND, "Ese usuario no está activo");
        }

        return sendEmailToUser(usuarioOpt.get());
    }

    // ⭐ MÉTODO PARA ENVIAR EMAIL A CLIENTE
    private ApiResponse sendEmailToCliente(Cliente cliente) {
        EmailValuesDTO dto = new EmailValuesDTO();
        dto.setMailFrom(mailFrom);
        dto.setMailTo(cliente.getCorreo());
        dto.setSubject(subject);
        dto.setUserName(cliente.getNombre() + " " + cliente.getApellido());

        UUID uuid = UUID.randomUUID();
        String tokenPassword = uuid.toString();
        dto.setTokenPassword(tokenPassword);
        
        // ⭐ GUARDAR TOKEN EN CLIENTE
        cliente.setContrasena(tokenPassword);
        clienteRepository.save(cliente);

        sendEmail(dto);
        LOGGER.info("Correo electrónico enviado exitosamente a cliente: {}", dto.getMailTo());
        return new ApiResponse("Te hemos enviado un correo");
    }

    // ⭐ MÉTODO PARA ENVIAR EMAIL A USUARIO
    private ApiResponse sendEmailToUser(User user) {
        EmailValuesDTO dto = new EmailValuesDTO();
        dto.setMailFrom(mailFrom);
        dto.setMailTo(user.getEmail());
        dto.setSubject(subject);
        dto.setUserName(user.getUsername());

        UUID uuid = UUID.randomUUID();
        String tokenPassword = uuid.toString();
        dto.setTokenPassword(tokenPassword);
        
        user.setTokenPassword(tokenPassword);
        userRepository.save(user);

        sendEmail(dto);
        LOGGER.info("Correo electrónico enviado exitosamente a usuario: {}", dto.getMailTo());
        return new ApiResponse("Te hemos enviado un correo");
    }

    public ApiResponsePassWord changePassword(ChangePasswordDTO dto) {
        LOGGER.info("Cambiando contraseña");

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            LOGGER.warn("Las contraseñas no coinciden");
            return new ApiResponsePassWord("Las contraseñas no coinciden", HttpStatus.BAD_REQUEST);
        }

        // ⭐ BUSCAR PRIMERO EN CLIENTES
        Optional<Cliente> clienteOpt = clienteRepository.findByContrasena(dto.getTokenPassword());
        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            String newPassword = passwordEncoder.encode(dto.getPassword());
            cliente.setContrasena(newPassword);
            cliente.setContrasena(null);
            clienteRepository.save(cliente);
            LOGGER.info("Contraseña actualizada para el cliente {}", cliente.getNombre());
            return new ApiResponsePassWord("Contraseña actualizada correctamente.", HttpStatus.OK);
        }

        // ⭐ SI NO ENCUENTRA EN CLIENTES, BUSCAR EN USUARIOS
        Optional<User> userOpt = userRepository.findByTokenPassword(dto.getTokenPassword());
        if (!userOpt.isPresent()) {
            LOGGER.warn("No se encontró usuario ni cliente con token {}", dto.getTokenPassword());
            return new ApiResponsePassWord("Usuario no existe o token expirado", HttpStatus.BAD_REQUEST);
        }

        User user = userOpt.get();
        String newPassword = passwordEncoder.encode(dto.getPassword());
        user.setPassword(newPassword);
        user.setTokenPassword(null);
        userRepository.save(user);

        LOGGER.info("Contraseña actualizada para el usuario {}", user.getUsername());
        return new ApiResponsePassWord("Contraseña actualizada correctamente.", HttpStatus.OK);
    }

    public void sendEmail(EmailValuesDTO dto) {
        // ⭐ ESTE MÉTODO QUEDA IGUAL
        LOGGER.info("Enviando correo electrónico");

        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            Context context = new Context();
            Map<String, Object> model = new HashMap<>();
            model.put("userName", dto.getUserName());
            model.put("url", "https://ferreteriachris.vercel.app/changepassword?tokenPassword=" + dto.getTokenPassword()
                    +"#/changepassword?tokenPassword="+dto.getTokenPassword());
            context.setVariables(model);
            String htmlText = templateEngine.process("email-template", context);
            helper.setFrom(dto.getMailFrom());
            helper.setTo(dto.getMailTo());
            helper.setSubject(dto.getSubject());
            helper.setText(htmlText, true);

            javaMailSender.send(message);

            LOGGER.info("Correo electrónico enviado a {}", dto.getMailTo());
        } catch (MessagingException e) {
            LOGGER.error("Error al enviar el correo electrónico", e);
        }
    }
}