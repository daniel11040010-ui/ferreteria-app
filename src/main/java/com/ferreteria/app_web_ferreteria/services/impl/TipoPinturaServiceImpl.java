package com.ferreteria.app_web_ferreteria.services.impl;

import com.ferreteria.app_web_ferreteria.model.TipoPintura;
import com.ferreteria.app_web_ferreteria.repository.TipoPinturaRepository;
import com.ferreteria.app_web_ferreteria.services.TipoPinturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TipoPinturaServiceImpl implements TipoPinturaService {

    @Autowired
    private TipoPinturaRepository tipoPinturaRepository;

    @Override
    public List<TipoPintura> findAll() {
        return tipoPinturaRepository.findAll();
    }

    @Override
    public Optional<TipoPintura> findById(Long id) {
        return tipoPinturaRepository.findById(id);
    }
}
