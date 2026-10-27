package com.aydsii.demo.service;

import java.io.Serial;

/** Consultar estado del pedido: el pedido no existe (404). */
public class RecursoNoEncontradoException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    public static RecursoNoEncontradoException pedido(Long idPedido) {
        return new RecursoNoEncontradoException("No existe el pedido " + idPedido + ".");
    }
}
