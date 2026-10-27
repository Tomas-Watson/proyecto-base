package com.aydsii.demo.dto;

import com.aydsii.demo.domain.ItemCarrito;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

/**
 * «DTO» ResumenCompraDTO: el detalle que el sistema presenta
 * antes de que el cliente confirme la compra (pasos 2 y 3 del flujo principal).
 */
@JsonPropertyOrder({"items", "montoTotal", "importeAAbonar", "hayStockSuficiente", "plazoReservaHoras"})
@Schema(description = "Resumen de la compra que se muestra antes de confirmar")
public class ResumenCompraDTO {

    @Schema(description = "Productos publicados del carrito con su cantidad y subtotal. "
            + "Los que ya no están publicados no aparecen (FE-02)")
    private final List<ItemCarrito> items;

    @Schema(description = "Total general: suma de los subtotales (RN-01)", example = "20600.00")
    private final BigDecimal montoTotal;

    @Schema(description = "100 % del total si hay stock de todo; seña del 30 % si falta stock (RN-03)",
            example = "20600.00")
    private final BigDecimal importeAAbonar;

    @Schema(description = "false si la cantidad pedida de algún producto supera su stock disponible (FA-02)",
            example = "true")
    private final Boolean hayStockSuficiente;

    @Schema(description = "Horas que se reservará el stock al confirmar (RN-04)", example = "24")
    private final Integer plazoReservaHoras;

   
    public ResumenCompraDTO(List<ItemCarrito> items, BigDecimal montoTotal, BigDecimal importeAAbonar,
                            Boolean hayStockSuficiente, Integer plazoReservaHoras) {
        this.items = items;
        this.montoTotal = montoTotal;
        this.importeAAbonar = importeAAbonar;
        this.hayStockSuficiente = hayStockSuficiente;
        this.plazoReservaHoras = plazoReservaHoras;
    }

    public List<ItemCarrito> getItems() {
        return items;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public BigDecimal getImporteAAbonar() {
        return importeAAbonar;
    }

    public Boolean getHayStockSuficiente() {
        return hayStockSuficiente;
    }

    public Integer getPlazoReservaHoras() {
        return plazoReservaHoras;
    }
}
