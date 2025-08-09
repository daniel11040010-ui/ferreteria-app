package com.ferreteria.app_web_ferreteria.services;

import com.ferreteria.app_web_ferreteria.model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface ClienteService {
    List<Cliente> findAllClientes();
    Page<Cliente> findAllClientes(Pageable pageable);
    Optional<Cliente> findClienteById(Long id);
    Cliente saveCliente(Cliente cliente);
    Cliente updateCliente(Cliente cliente);
    void deleteCliente(Long id);
    List<Cliente> buscarClientePorFiltro(String filtro);
    Page<Cliente> listarClientesEstado(Boolean estado, Pageable pageable);
    Page<Cliente> filtrarCliente(String textoBuscar, Pageable pageable);
    boolean existsByCorreo(String correo);
    boolean existsByDocumentoIdentidad(String documentoIdentidad);
    boolean existsByNombreAndApellido(String nombre, String apellido);
    Optional<Cliente> loginCliente(String correo, String contrasena);
} 