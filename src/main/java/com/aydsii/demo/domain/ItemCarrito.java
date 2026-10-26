package com.aydsii.demo.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * «Entity» ItemCarrito del diagrama de clases de diseño (tabla {@code item_carrito} del DER):
 * un producto agregado al carrito con su cantidad.
 */
@Entity
@Table(name = "item_carrito")
@JsonPropertyOrder({"producto", "cantidad", "subtotal"})
@Schema(description = "Producto del carrito con su cantidad y subtotal")
public class ItemCarrito {

    @EmbeddedId
    @JsonIgnore
    private ItemCarritoId id = new ItemCarritoId();

    @MapsId("idCarrito")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_carrito")
    @JsonIgnore
    private Carrito carrito;

    @MapsId("idProducto")
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_producto")
    @Schema(description = "Producto agregado al carrito")
    private Producto producto;

    @NotNull
    @Positive
    @Column(name = "cantidad", nullable = false)
    @Schema(description = "Unidades pedidas", example = "2")
    private Integer cantidad;

    /** Constructor requerido por JPA. */
    protected ItemCarrito() {
    }

    public ItemCarrito(Carrito carrito, Producto producto, Integer cantidad) {
        this.carrito = carrito;
        this.producto = producto;
        this.cantidad = cantidad;
    }

    /**
     * Subtotal del renglón: precio unitario vigente × cantidad (RN-01, mensaje [5] del diagrama
     * de secuencia). En el JSON se publica como la propiedad {@code subtotal}.
     */
    @JsonProperty("subtotal")
    @Schema(description = "Precio unitario vigente × cantidad (RN-01)", example = "9000.00")
    public BigDecimal calcularSubtotal() {
        return producto.getPrecio().multiply(BigDecimal.valueOf(cantidad));
    }

    public Producto getProducto() {
        return producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }
}
