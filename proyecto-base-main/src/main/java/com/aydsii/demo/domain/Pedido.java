package com.aydsii.demo.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * «Entity» Pedido de los diagramas de clases de diseño de Comprar y de Consultar estado del pedido
 * ({pedido} del DER). Es una sola clase para los dos casos de uso: cada diagrama muestra los
 * atributos y métodos que usa su caso de uso.
 *
 * Es lo que devuelve {confirmarCompra}: el número de pedido, el importe a abonar y el
 * vencimiento de la reserva, que es el plazo para pagar (paso 7 del flujo principal de Comprar).
 * También es lo que lista {listarPedidos} en Consultar estado del pedido, cuyo diagrama muestra
 * solo idPedido, fechaHora, estado, montoTotal y la asociación con el cliente.
 */
@Entity
@Table(name = "pedido")
@JsonPropertyOrder({"idPedido", "estado", "fechaHora", "vencimientoReserva", "fechaCancelacion", "cliente",
        "detalles", "cantidadProductos", "montoTotal", "importeAAbonar"})
@Schema(description = "Pedido del cliente")
public class Pedido {

    /** Plazo máximo de la reserva de stock, en horas */
    public static final int PLAZO_RESERVA_HORAS = 24;

    /** Porción del total que se abona como seña cuando falta stock de algún producto */
    public static final BigDecimal PORCENTAJE_SENA = new BigDecimal("0.30");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    @Schema(description = "Número de pedido", example = "1")
    private Long idPedido;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    @Schema(description = "Cliente que realizó el pedido")
    private Cliente cliente;

    @NotNull
    @Column(name = "fecha_hora", nullable = false)
    @Schema(description = "Fecha y hora en que se generó el pedido", example = "2026-10-25T10:30:00")
    private LocalDateTime fechaHora;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    @Schema(description = "Estado del pedido", example = "PENDIENTE_DE_PAGO")
    private EstadoPedido estado;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "monto_total", nullable = false, precision = 12, scale = 2)
    @Schema(description = "Suma de los subtotales del detalle (RN-01)", example = "20600.00")
    private BigDecimal montoTotal;

    @NotNull
    @Positive
    @Column(name = "cantidad_productos", nullable = false)
    @Schema(description = "Cantidad total de unidades del pedido", example = "6")
    private Integer cantidadProductos;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "importe_a_abonar", nullable = false, precision = 12, scale = 2)
    @Schema(description = "100 % del total, o seña del 30 % si falta stock (RN-03)", example = "20600.00")
    private BigDecimal importeAAbonar;

    @Column(name = "vencimiento_reserva")
    @Schema(description = "Hasta cuándo se mantiene la reserva de stock: es el plazo para pagar (RN-04)",
            example = "2026-10-26T10:30:00")
    private LocalDateTime vencimientoReserva;

    @Column(name = "fecha_cancelacion")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Fecha y hora en que se canceló el pedido (solo si está CANCELADO)",
            example = "2026-10-20T16:00:00", nullable = true)
    private LocalDateTime fechaCancelacion;

    @NotEmpty
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "Detalle del pedido: productos, cantidades, subtotales y unidades reservadas")
    private List<DetallePedido> detalles = new ArrayList<>();

    /** Constructor requerido por JPA. */
    protected Pedido() {
    }

    /**
     * El pedido nace en «Pendiente de pago» y la reserva de
     * stock vence a las {@value #PLAZO_RESERVA_HORAS} horas 
     */
    public Pedido(Cliente cliente, LocalDateTime fechaHora) {
        this.cliente = cliente;
        this.fechaHora = fechaHora;
        this.estado = EstadoPedido.PENDIENTE_DE_PAGO;
        this.vencimientoReserva = fechaHora.plusHours(PLAZO_RESERVA_HORAS);
        this.montoTotal = BigDecimal.ZERO;
        this.cantidadProductos = 0;
        this.importeAAbonar = BigDecimal.ZERO;
    }

    /** agrega un renglón al detalle y acumula la cantidad de productos. */
    public void agregarDetalle(DetallePedido detalle) {
        detalle.setPedido(this);
        detalles.add(detalle);
        cantidadProductos += detalle.getCantidad();
    }

    /**el monto total es la suma de los subtotales y no se carga a mano*/
    public BigDecimal calcularMontoTotal() {
        montoTotal = detalles.stream()
                .map(DetallePedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        return montoTotal;
    }

    /**
     * si hay stock de todo se abona el 100 % del total; si no, una seña del 30 %
     * 
     *
     * @throws IllegalArgumentException si no se indica si hay stock
     */
    public BigDecimal calcularImporteAAbonar(Boolean hayStock) {
        if (hayStock == null) {
            throw new IllegalArgumentException("Hay que indicar si hay stock suficiente");
        }
        BigDecimal total = calcularMontoTotal();
        importeAAbonar = hayStock
                ? total
                : total.multiply(PORCENTAJE_SENA).setScale(2, RoundingMode.HALF_UP);
        return importeAAbonar;
    }

    // ------------------------------------------------------------ Getters

    public Long getIdPedido() {
        return idPedido;
    }

    public LocalDateTime getFechaCancelacion() {
        return fechaCancelacion;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public Integer getCantidadProductos() {
        return cantidadProductos;
    }

    public BigDecimal getImporteAAbonar() {
        return importeAAbonar;
    }

    public LocalDateTime getVencimientoReserva() {
        return vencimientoReserva;
    }

    public List<DetallePedido> getDetalles() {
        return detalles;
    }
}
