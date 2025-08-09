package com.ferreteria.app_web_ferreteria.security.repository;


import com.ferreteria.app_web_ferreteria.security.entity.Rol;
import com.ferreteria.app_web_ferreteria.security.enums.RolNombre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {

    Optional<Rol> findByRolName(RolNombre rolNombre);
}
