package com.aydsii.demo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Clave primaria compuesta de {@code detalle_pedido} (id_pedido + id_producto), tal como figura
 * en el DER. Es un detalle de persistencia: no aparece en el diagrama de clases.
 */
@Embeddable
public class DetallePedidoId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "id_pedido")
    private Long idPedido;

    @Column(name = "id_producto")
    private Long idProducto;

    public DetallePedidoId() {
    }

    public DetallePedidoId(Long idPedido, Long idProducto) {
        this.idPedido = idPedido;
        this.idProducto = idProducto;
    }

    public Long getIdPedido() {
        return idPedido;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof DetallePedidoId that)) {
            return false;
        }
        return Objects.equals(idPedido, that.idPedido) && Objects.equals(idProducto, that.idProducto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPedido, idProducto);
    }
}
