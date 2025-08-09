package com.ferreteria.app_web_ferreteria.security.service;

import com.ferreteria.app_web_ferreteria.security.entity.User;
import com.ferreteria.app_web_ferreteria.security.entity.UserMain;
import com.ferreteria.app_web_ferreteria.security.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String nombreOrEmail) throws UsernameNotFoundException {
        LOGGER.info("Cargando detalles de usuario por nombre de usuario o correo electrónico: {}", nombreOrEmail);

        User user = userRepository.findByUsernameOrEmail(nombreOrEmail, nombreOrEmail)
                .orElseThrow(() -> {
                    LOGGER.warn("Usuario con nombre de usuario o correo electrónico {} no encontrado", nombreOrEmail);
                    return new UsernameNotFoundException("Ese usuario no existe");
                });

        LOGGER.info("Detalles de usuario cargados exitosamente para: {}", nombreOrEmail);
        return UserMain.build(user);
    }

}
