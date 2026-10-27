package com.aydsii.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ApiResponseDTO", description = "Estructura estándar para las respuestas de la API")
public class ApiResponseDTO<T> {

    @Schema(description = "Código de estado HTTP", example = "200")
    private int status;

    @Schema(description = "Mensaje descriptivo del resultado de la operación", example = "Operación exitosa")
    private String message;

    @Schema(description = "Carga útil con los datos de la respuesta")
    private T data;
}