package com.ferreteria.app_web_ferreteria.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferreteria.app_web_ferreteria.security.dto.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/*
* Se encarga de comprobar si hay un token y si no lo hay devuelve 401 no authorized
*/
@Component
public class JwtEntryPoint implements AuthenticationEntryPoint {

    private final static Logger logger = LoggerFactory.getLogger(JwtEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        logger.error("Fail en el método commence: {}", authException.getMessage());
        ApiResponse mensaje = new ApiResponse("token inválido o vacío");
        response.setContentType("application/json");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(new ObjectMapper().writeValueAsString(mensaje));
        response.getWriter().flush();
        response.getWriter().close();
    }
}