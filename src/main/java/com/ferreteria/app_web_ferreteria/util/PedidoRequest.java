package com.ferreteria.app_web_ferreteria.util;

import java.util.List;

import com.ferreteria.app_web_ferreteria.model.Pedido;
import com.ferreteria.app_web_ferreteria.model.PedidoDetalle;

public class PedidoRequest {
    private Pedido pedido;
    private List<PedidoDetalle> detalles;

    public Pedido getPedido() {
        return pedido;
    }

    public List<PedidoDetalle> getDetalles() {
        return detalles;
    }
}
