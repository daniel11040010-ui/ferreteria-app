package com.ferreteria.app_web_ferreteria.repository;

import com.ferreteria.app_web_ferreteria.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    @Query(value = "SELECT * FROM cliente WHERE nombre LIKE CONCAT('%', :filtro, '%') OR apellido LIKE CONCAT('%', :filtro, '%') OR correo LIKE CONCAT('%', :filtro, '%') OR documento_identidad LIKE CONCAT('%', :filtro, '%')", nativeQuery = true)
    List<Cliente> buscarClientePorFiltro(@Param("filtro") String filtro);
    List<Cliente> findByEstaActivoOrderByIdDesc(Boolean estaActivo);
    boolean existsByCorreo(String correo);
    boolean existsByDocumentoIdentidad(String documentoIdentidad);
    boolean existsByNombreAndApellido(String nombre, String apellido);
    Optional<Cliente> findByCorreo(String correo);
    Optional<Cliente> findByContrasena(String contrasena);
} 