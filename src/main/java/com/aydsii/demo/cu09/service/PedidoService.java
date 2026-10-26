package com.aydsii.demo.cu09.service;

import com.aydsii.demo.cu09.dominio.Pedido;
import com.aydsii.demo.cu09.dto.EstadoPedidoDTO;
import com.aydsii.demo.cu09.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoService {

   private final PedidoRepository pedidoRepository;

    // Inyección de dependencia por constructor 
    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

   
    public List<Pedido> listarPedidos(Long idUsuario) {
        return pedidoRepository.findByClienteIdUsuario(idUsuario);
    }

    public EstadoPedidoDTO consultarEstado(Long idPedido, Long idUsuario) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        // Validar que el pedido pertenezca al usuario (RN-01)
        if (!pedido.getCliente().getIdUsuario().equals(idUsuario)) {
            throw new RuntimeException("No autorizado para consultar este pedido");
        }

        EstadoPedidoDTO dto = new EstadoPedidoDTO();
        dto.setIdPedido(pedido.getIdPedido());
        dto.setFechaHora(pedido.getFechaHora());
        dto.setEstado(pedido.getEstado());
        dto.setMontoTotal(pedido.getMontoTotal());

        // Lógica de los flujos alternativos según el estado del pedido
        String estado = pedido.getEstado();
        
        if ("Pendiente de pago".equals(estado)) {
            dto.setInformacionComplementaria(
                "Importe a abonar: 100% del total." +
                "Todavía puedes cancelar este pedido."
            );
        } else if ("En producción".equals(estado) || "En cola de producción".equals(estado)) {
            dto.setInformacionComplementaria(
                "El pedido se encuentra en producción. " +
                "El saldo restante del 70% se abonará al completar la fabricación. " +
                "Verifica las condiciones de cancelación y reembolso de seña."
            );
        } else if ("Cancelado".equals(estado)) {
            dto.setInformacionComplementaria(
                "Este pedido fue cancelado. Consulta la fecha de cancelación y el estado del reembolso."
            );
        } else if (estado != null && (estado.contains("Despachado") || estado.contains("Entregado") || estado.contains("Listo"))) {
            dto.setInformacionComplementaria(
                "El detalle del envío puede consultarse en el módulo 'Consultar estado del envío'."
            );
        } else {
            dto.setInformacionComplementaria("Estado actual registrado correctamente en el sistema.");
        }

        return dto;
    }
}