package com.ferreteria.app_web_ferreteria.services;

import com.ferreteria.app_web_ferreteria.model.Marca;

import java.util.List;
import java.util.Optional;

public interface MarcaService {
    List<Marca> findAll();
    Optional<Marca> findById(Long id);
}
