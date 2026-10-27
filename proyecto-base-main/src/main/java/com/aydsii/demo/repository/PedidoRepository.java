package com.aydsii.demo.repository;

import com.aydsii.demo.domain.Pedido;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * «Repository» PedidoRepository de los diagramas de clases de diseño. Es uno solo para los dos
 * casos de uso: Comprar usa {@code save} y Consultar estado del pedido usa {@code findById} y
 * {@code findByClienteIdUsuario}. Extiende {@link org.springframework.data.repository.Repository}
 * (y no JpaRepository) para exponer solo los métodos que aparecen en los diagramas.
 */
@Repository
public interface PedidoRepository extends org.springframework.data.repository.Repository<Pedido, Long> {

    /** Comprar: registra el pedido con su detalle (mensaje [27] del diagrama de secuencia). */
    Pedido save(Pedido pedido);

    /** Consultar estado del pedido: busca un pedido por su número. */
    Optional<Pedido> findById(Long idPedido);

    /**
     * Consultar estado del pedido: pedidos del cliente. Spring Data arma la consulta a partir del
     * nombre (Pedido.cliente.idUsuario).
     */
    List<Pedido> findByClienteIdUsuario(Long idUsuario);
}
