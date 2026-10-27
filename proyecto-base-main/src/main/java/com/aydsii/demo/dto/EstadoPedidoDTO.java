package com.aydsii.demo.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * «DTO» EstadoPedidoDTO del diagrama de clases de diseño de CU-09 Consultar estado del pedido: el
 * estado vigente del pedido con sus datos principales y, en {@code informacionComplementaria}, lo
 * que corresponde informar según el estado (flujos alternativos FA-02 a FA-06).
 *
 * Los tipos son los del diagrama (long, String, double). El constructor recibe los datos
 * principales, como el mensaje «create» [18] del diagrama de secuencia.
 */
@JsonPropertyOrder({"idPedido", "fechaHora", "estado", "montoTotal", "informacionComplementaria"})
@Schema(description = "Estado vigente del pedido y la información complementaria según el estado")
public class EstadoPedidoDTO {

    @Schema(description = "Número de pedido", example = "2")
    private final long idPedido;

    @Schema(description = "Fecha y hora en que se realizó el pedido", example = "2026-10-20T10:00:00")
    private final LocalDateTime fechaHora;

    @Schema(description = "Estado vigente del pedido (RN-02)", example = "EN_COLA_DE_PRODUCCION")
    private final String estado;

    @Schema(description = "Monto total del pedido", example = "185000.0")
    private final double montoTotal;

    @Schema(description = "Lo que corresponde informar según el estado (FA-02 a FA-06)",
            example = "Tiempo estimado de producción: 10 días. El saldo restante de $129500.00 (70 %) podrá "
                    + "abonarse cuando se complete la producción. Todavía podés cancelar el pedido: se te "
                    + "reembolsaría el 100 % de la seña (RN-04).")
    private String informacionComplementaria;

   

    public EstadoPedidoDTO(long idPedido, LocalDateTime fechaHora, String estado, double montoTotal) {
        this.idPedido = idPedido;
        this.fechaHora = fechaHora;
        this.estado = estado;
        this.montoTotal = montoTotal;
    }

    public long getIdPedido() {
        return idPedido;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getEstado() {
        return estado;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public String getInformacionComplementaria() {
        return informacionComplementaria;
    }

    public void setInformacionComplementaria(String informacionComplementaria) {
        this.informacionComplementaria = informacionComplementaria;
    }
}
