package com.aydsii.demo.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

/**
 * «Entity» DetallePedido del diagrama de clases de diseño (tabla {@code detalle_pedido} del DER):
 * un renglón del pedido con el producto, la cantidad, el precio vigente al confirmar, el subtotal
 * y las unidades que se pudieron reservar.
 */
@Entity
@Table(name = "detalle_pedido")
@JsonPropertyOrder({"producto", "cantidad", "precioUnitario", "subtotal", "cantidadReservada"})
@Schema(description = "Renglón del pedido")
public class DetallePedido {

    @EmbeddedId
    @JsonIgnore
    private DetallePedidoId id = new DetallePedidoId();

    @MapsId("idPedido")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pedido")
    @JsonIgnore
    private Pedido pedido;

    @MapsId("idProducto")
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_producto")
    @Schema(description = "Producto vendido")
    private Producto producto;

    @NotNull
    @Positive
    @Column(name = "cantidad", nullable = false)
    @Schema(description = "Unidades pedidas", example = "3")
    private Integer cantidad;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    @Schema(description = "Precio vigente al confirmar la compra (RN-02)", example = "2800.00")
    private BigDecimal precioUnitario;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    @Schema(description = "Precio unitario × cantidad (RN-01)", example = "8400.00")
    private BigDecimal subtotal;

    @NotNull
    @PositiveOrZero
    @Column(name = "cantidad_reservada", nullable = false)
    @Schema(description = "Unidades reservadas. Si es menor que la cantidad, faltó stock (FA-02)", example = "3")
    private Integer cantidadReservada;

    /** Constructor requerido por JPA. */
    protected DetallePedido() {
    }

    /** Mensaje [21] del diagrama de secuencia: el subtotal se calcula, no se carga a mano (RN-01). */
    public DetallePedido(Producto producto, Integer cantidad, BigDecimal precioUnitario, Integer cantidadReservada) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.cantidadReservada = cantidadReservada;
        this.subtotal = calcularSubtotal();
    }

    /** Precio unitario × cantidad (RN-01). Es final porque se usa en el constructor. */
    public final BigDecimal calcularSubtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    /** Lo usa {@link Pedido#agregarDetalle(DetallePedido)} para mantener la composición. */
    void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Producto getProducto() {
        return producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public Integer getCantidadReservada() {
        return cantidadReservada;
    }
}
