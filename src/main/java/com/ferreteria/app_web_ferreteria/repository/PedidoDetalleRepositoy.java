package com.ferreteria.app_web_ferreteria.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ferreteria.app_web_ferreteria.model.PedidoDetalle;

public interface PedidoDetalleRepositoy  extends JpaRepository<PedidoDetalle, Long>{
    
}
