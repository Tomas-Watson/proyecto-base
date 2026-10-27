package com.aydsii.demo.service;

import java.io.Serial;

/** RN-01 de Consultar estado del pedido: un cliente solo puede consultar sus propios pedidos (403). */
public class AccesoDenegadoException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }

    public static AccesoDenegadoException pedidoAjeno(Long idUsuario, Long idPedido) {
        return new AccesoDenegadoException("El pedido " + idPedido + " no pertenece al cliente " + idUsuario
                + ". Un cliente solo puede consultar sus propios pedidos.");
    }
}
