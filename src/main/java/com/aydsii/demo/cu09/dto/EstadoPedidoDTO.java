package com.aydsii.demo.cu09.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class EstadoPedidoDTO {
    private Long idPedido;
    private LocalDateTime fechaHora;
    private String estado;
    private BigDecimal montoTotal;
    
    // Campo para mostrar los flujos alternativos y reglas de negocio
    private String informacionComplementaria;
}