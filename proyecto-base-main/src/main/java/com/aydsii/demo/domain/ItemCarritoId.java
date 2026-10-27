package com.aydsii.demo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Clave primaria compuesta de {item_carrito} (id_carrito + id_producto)
 */
@Embeddable
public class ItemCarritoId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "id_carrito")
    private Long idCarrito;

    @Column(name = "id_producto")
    private Long idProducto;

    public ItemCarritoId() {
    }

    public ItemCarritoId(Long idCarrito, Long idProducto) {
        this.idCarrito = idCarrito;
        this.idProducto = idProducto;
    }

    public Long getIdCarrito() {
        return idCarrito;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof ItemCarritoId that)) {
            return false;
        }
        return Objects.equals(idCarrito, that.idCarrito) && Objects.equals(idProducto, that.idProducto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCarrito, idProducto);
    }
}
