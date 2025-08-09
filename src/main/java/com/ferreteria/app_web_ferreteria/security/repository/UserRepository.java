package com.ferreteria.app_web_ferreteria.security.repository;

import com.ferreteria.app_web_ferreteria.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameOrEmail(String username, String email);
    Optional<User> findByTokenPassword(String tokenPassword);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByDocument(String document);
    boolean existsByNameAndLastname(String nombre, String apellido);

    List<User> findByIsActiveOrderByIdDesc(boolean activo);

    @Query(value = "CALL BuscarUsuariosPorFiltro(:searchText)", nativeQuery = true)
    List<User> searchUsuario(@Param("searchText") String searchText);

    @Query(value = "SELECT * FROM user u WHERE u.email = :email AND u.is_active = true", nativeQuery = true)
    User findByCorreoElectronicoAndEstado(@Param("email") String email);

    @Query(value = "SELECT * FROM user WHERE email = :email", nativeQuery = true)
    User findByCorreoElectronico(@Param("email") String email);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.rolName = 'VENDEDOR' AND u.isActive = true")
    List<User> findActiveVendedores();

}
