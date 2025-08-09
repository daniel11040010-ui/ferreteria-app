package com.ferreteria.app_web_ferreteria.services;

import com.ferreteria.app_web_ferreteria.model.TipoPintura;

import java.util.List;
import java.util.Optional;

public interface TipoPinturaService {
    List<TipoPintura> findAll();
    Optional<TipoPintura> findById(Long id);
}
