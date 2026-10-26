package com.aydsii.demo.repository;

import com.aydsii.demo.domain.Pedido;
import org.springframework.stereotype.Repository;

/**
 * «Repository» PedidoRepository del diagrama de clases de diseño.
 */
@Repository
public interface PedidoRepository extends org.springframework.data.repository.Repository<Pedido, Long> {

    /** Guarda el pedido junto con su detalle (mensaje [27] del diagrama de secuencia). */
    Pedido save(Pedido pedido);
}
