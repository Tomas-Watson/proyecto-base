package com.aydsii.demo.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Respuesta uniforme de la API cuando algo sale mal.
 */
@JsonPropertyOrder({"status", "titulo", "mensaje", "detalles", "ruta", "fecha"})
@Schema(description = "Respuesta de error de la API")
public record ErrorDTO(

        @Schema(description = "Código de estado HTTP", example = "409")
        int status,

        @Schema(description = "Título corto del error", example = "Carrito vacío")
        String titulo,

        @Schema(description = "Explicación para el usuario",
                example = "El carrito está vacío: agrega productos antes de finalizar la compra.")
        String mensaje,

        @Schema(description = "Datos adicionales: productos que ya no están publicados (FE-02) o campos inválidos")
        List<String> detalles,

        @Schema(description = "Ruta de la solicitud", example = "/api/pedidos/resumen-compra/5")
        String ruta,

        @Schema(description = "Fecha y hora del error", example = "2026-10-25T10:30:00")
        LocalDateTime fecha) {

    public static ErrorDTO de(int status, String titulo, String mensaje, String ruta, List<String> detalles) {
        return new ErrorDTO(status, titulo, mensaje, List.copyOf(detalles), ruta,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    }
}
