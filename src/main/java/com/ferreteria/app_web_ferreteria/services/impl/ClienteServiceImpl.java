package com.ferreteria.app_web_ferreteria.services.impl;

import com.ferreteria.app_web_ferreteria.model.Cliente;
import com.ferreteria.app_web_ferreteria.repository.ClienteRepository;
import com.ferreteria.app_web_ferreteria.services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<Cliente> findAllClientes() {
        return clienteRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    @Override
    public Page<Cliente> findAllClientes(Pageable pageable) {
        return clienteRepository.findAll(pageable);
    }

    @Override
    public Optional<Cliente> findClienteById(Long id) {
        return clienteRepository.findById(id);
    }

    @Override
    public Cliente saveCliente(Cliente cliente) {
        if (cliente.getContrasena() != null && !cliente.getContrasena().startsWith("$2a$")) {
            cliente.setContrasena(passwordEncoder.encode(cliente.getContrasena()));
        }
        return clienteRepository.save(cliente);
    }

    @Override
    public Cliente updateCliente(Cliente cliente) {
        Cliente existente = clienteRepository.findById(cliente.getId()).orElse(null);
        if (existente != null) {
            existente.setNombre(cliente.getNombre());
            existente.setApellido(cliente.getApellido());
            existente.setCorreo(cliente.getCorreo());
            if (cliente.getContrasena() != null && !cliente.getContrasena().equals(existente.getContrasena())) {
                if (!cliente.getContrasena().startsWith("$2a$")) {
                    existente.setContrasena(passwordEncoder.encode(cliente.getContrasena()));
                } else {
                    existente.setContrasena(cliente.getContrasena());
                }
            }
            existente.setTelefono(cliente.getTelefono());
            existente.setDocumentoIdentidad(cliente.getDocumentoIdentidad());
            existente.setDireccion(cliente.getDireccion());
            existente.setEstaActivo(cliente.getEstaActivo());
            return clienteRepository.save(existente);
        }
        return null;
    }

    @Override
    public void deleteCliente(Long id) {
        clienteRepository.deleteById(id);
    }

    @Override
    public List<Cliente> buscarClientePorFiltro(String filtro) {
        return clienteRepository.buscarClientePorFiltro(filtro);
    }

    @Override
    public Page<Cliente> listarClientesEstado(Boolean estado, Pageable pageable) {
        List<Cliente> clientes = clienteRepository.findByEstaActivoOrderByIdDesc(estado);
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Cliente> sublist;

        if (clientes.size() < startItem) {
            sublist = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, clientes.size());
            sublist = clientes.subList(startItem, toIndex);
        }

        return new PageImpl<>(sublist, pageable, clientes.size());
    }

    @Override
    public Page<Cliente> filtrarCliente(String textoBuscar, Pageable pageable) {
        List<Cliente> clientes = clienteRepository.buscarClientePorFiltro(textoBuscar);
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Cliente> sublist;

        if (clientes.size() < startItem) {
            sublist = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, clientes.size());
            sublist = clientes.subList(startItem, toIndex);
        }

        return new PageImpl<>(sublist, pageable, clientes.size());
    }

    @Override
    public boolean existsByCorreo(String correo) {
        return clienteRepository.existsByCorreo(correo);
    }

    @Override
    public boolean existsByDocumentoIdentidad(String documentoIdentidad) {
        return clienteRepository.existsByDocumentoIdentidad(documentoIdentidad);
    }

    @Override
    public boolean existsByNombreAndApellido(String nombre, String apellido) {
        return clienteRepository.existsByNombreAndApellido(nombre, apellido);
    }

    @Override
    public Optional<Cliente> loginCliente(String correo, String contrasena) {
        Optional<Cliente> clienteOpt = clienteRepository.findByCorreo(correo);
        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            if (passwordEncoder.matches(contrasena, cliente.getContrasena())) {
                return Optional.of(cliente);
            }
        }
        return Optional.empty();
    }
} 