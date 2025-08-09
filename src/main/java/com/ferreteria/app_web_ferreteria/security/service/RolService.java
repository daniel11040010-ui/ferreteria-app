package com.ferreteria.app_web_ferreteria.security.service;


import com.ferreteria.app_web_ferreteria.security.entity.Rol;
import com.ferreteria.app_web_ferreteria.security.enums.RolNombre;
import com.ferreteria.app_web_ferreteria.security.repository.RolRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RolService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RolService.class);

    @Autowired
    private RolRepository rolRepository;

    public Optional<Rol> getByRolName(RolNombre rolNombre){
        LOGGER.info("Obteniendo rol por nombre: {}", rolNombre);
        return rolRepository.findByRolName(rolNombre);
    }

    public void save(Rol rol){
        LOGGER.info("Guardando rol: {}", rol);
        rolRepository.save(rol);
    }

    public List<Rol> getRoles(){
        return rolRepository.findAll();
    }

}
