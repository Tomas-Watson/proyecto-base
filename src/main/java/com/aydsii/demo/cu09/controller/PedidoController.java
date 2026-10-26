package com.aydsii.demo.cu09.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Positive;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse; // Anotación de Swagger

import com.aydsii.demo.cu09.dto.ApiResponseDTO; // Tu clase DTO de respuesta
import com.aydsii.demo.cu09.dto.EstadoPedidoDTO;
import com.aydsii.demo.cu09.service.PedidoService;

import java.util.List;

@RestController
@RequestMapping("/api/cu09/usuarios")
@Validated
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @Operation(summary = "Listar pedidos del usuario", description = "Devuelve la lista de pedidos asociados al ID de usuario.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedidos recuperados con éxito"),
        @ApiResponse(responseCode = "400", description = "ID de usuario inválido"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado o sin pedidos")
    })
    @GetMapping("/{idUsuario}/pedidos")
    public ResponseEntity<ApiResponseDTO<List<com.aydsii.demo.cu09.dominio.Pedido>>> listarPedidos(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable @Positive(message = "El ID de usuario debe ser mayor a 0.") Long idUsuario) {
        
        var pedidos = pedidoService.listarPedidos(idUsuario);
        ApiResponseDTO<List<com.aydsii.demo.cu09.dominio.Pedido>> response = 
            new ApiResponseDTO<>(200, "Pedidos recuperados con éxito", pedidos);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Consultar estado detallado de un pedido", description = "Verifica la propiedad (RN-01) y devuelve los flujos alternativos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado consultado con éxito"),
        @ApiResponse(responseCode = "400", description = "IDs inválidos (<= 0)"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado o no pertenece al usuario")
    })
    @GetMapping("/{idUsuario}/pedidos/{idPedido}/estado")
    public ResponseEntity<ApiResponseDTO<EstadoPedidoDTO>> consultarEstado(
            @Parameter(description = "ID único del usuario", example = "1")
            @PathVariable("idUsuario") @Positive(message = "El ID de usuario debe ser mayor a 0.") Long idUsuario, 
            
            @Parameter(description = "ID único del pedido", example = "10")
            @PathVariable("idPedido") @Positive(message = "El ID de pedido debe ser mayor a 0.") Long idPedido) {
        
        EstadoPedidoDTO dto = pedidoService.consultarEstado(idPedido, idUsuario);
        ApiResponseDTO<EstadoPedidoDTO> response = 
            new ApiResponseDTO<>(200, "Estado del pedido consultado con éxito", dto);
        
        return ResponseEntity.ok(response);
    }
}