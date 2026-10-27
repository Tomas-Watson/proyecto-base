package com.aydsii.demo.domain;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Estados posibles de un pedido, según el diagrama de estados de la clase Pedido.
 * El caso de uso Comprar solo se generan pedidos en {@link #PENDIENTE_DE_PAGO}.
 */
@Schema(description = "Estado del pedido. Al confirmar la compra el pedido queda en PENDIENTE_DE_PAGO.")
public enum EstadoPedido {
    PENDIENTE_DE_PAGO,
    EN_COLA_DE_PRODUCCION,
    EN_PRODUCCION,
    CONFIRMADO,
    LISTO_PARA_DESPACHO,
    DESPACHADO,
    EN_TRANSITO,
    ENTREGADO,
    CANCELADO
}
