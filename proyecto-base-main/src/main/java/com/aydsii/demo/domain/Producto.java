package com.aydsii.demo.domain;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;

import java.math.BigDecimal;

/**
 * «Entity» Producto del diagrama de clases de diseño ({producto} del DER).
 * 
 * Se mapean los atributos que usa el caso de uso Comprar. Para evitar sobreventas cuando dos
 * clientes compran a la vez, la entidad usa bloqueo optimista sin columna de versión
 * ({@link OptimisticLockType#DIRTY}): al guardar la reserva, Hibernate agrega a la sentencia
 * UPDATE la condición de que el stock siga siendo el que se leyó. Si otro cliente lo cambió en el
 * medio, la compra falla y se revierte en lugar de pisar su reserva.
 */
@Entity
@Table(name = "producto")
@DynamicUpdate
@OptimisticLocking(type = OptimisticLockType.DIRTY)
@JsonPropertyOrder({"idProducto", "nombre", "precio", "publicado", "stockDisponible", "tiempoEstimadoProduccion"})
@Schema(description = "Producto del catálogo de VerdeGreen")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    @Schema(description = "Identificador del producto", example = "1")
    private Long idProducto;

    @NotBlank
    @Size(max = 100)
    @Column(name = "nombre", nullable = false, length = 100)
    @Schema(description = "Nombre del producto", example = "Maceta mediana de PET reciclado")
    private String nombre;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "precio", nullable = false, precision = 12, scale = 2)
    @Schema(description = "Precio vigente en el catálogo (RN-02)", example = "4500.00")
    private BigDecimal precio;

    @NotNull
    @Column(name = "publicado", nullable = false)
    @Schema(description = "Indica si el producto sigue publicado en el catálogo (FE-02)", example = "true")
    private Boolean publicado;

    @NotNull
    @PositiveOrZero
    @Column(name = "stock_disponible", nullable = false)
    @Schema(description = "Unidades disponibles, ya descontadas las reservadas", example = "50")
    private Integer stockDisponible;

    @PositiveOrZero
    @Column(name = "tiempo_estimado_produccion")
    @Schema(description = "Días hábiles de fabricación. Solo aplica a los productos que se fabrican a pedido (FA-02)",
            example = "10", nullable = true)
    private Integer tiempoEstimadoProduccion;

    /** Constructor requerido por JPA. */
    protected Producto() {
    }

    public Producto(Long idProducto, String nombre, BigDecimal precio, Boolean publicado,
                    Integer stockDisponible, Integer tiempoEstimadoProduccion) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.precio = precio;
        this.publicado = publicado;
        this.stockDisponible = stockDisponible;
        this.tiempoEstimadoProduccion = tiempoEstimadoProduccion;
    }

    /**
     * Indica si el stock disponible alcanza para la cantidad pedida
     * 
     */
    public Boolean hayStock(Integer cantidad) {
        return cantidad != null && stockDisponible != null && stockDisponible >= cantidad;
    }

    /**
     * Reserva hasta {cantidad} unidades y devuelve cuántas pudo reservar
     * Si el stock no alcanza, reserva únicamente lo disponible 
     *
     * @throws IllegalArgumentException si la cantidad no es mayor a cero
     */
    public Integer reservar(Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a reservar debe ser mayor a cero");
        }
        int disponible = stockDisponible == null ? 0 : stockDisponible;
        int cantidadReservada = Math.min(cantidad, disponible);
        stockDisponible = disponible - cantidadReservada;
        return cantidadReservada;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public Boolean getPublicado() {
        return publicado;
    }

    public Integer getStockDisponible() {
        return stockDisponible;
    }

    public Integer getTiempoEstimadoProduccion() {
        return tiempoEstimadoProduccion;
    }
}
