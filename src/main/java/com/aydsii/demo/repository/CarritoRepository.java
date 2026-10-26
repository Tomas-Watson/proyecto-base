package com.aydsii.demo.repository;

import com.aydsii.demo.domain.Carrito;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * «Repository» CarritoRepository del diagrama de clases de diseño.
 * <p>
 * Extiende el {@code Repository} base de Spring Data, y no {@code JpaRepository}, para exponer
 * exactamente los métodos del diagrama.
 */
@Repository
public interface CarritoRepository extends org.springframework.data.repository.Repository<Carrito, Long> {

    /**
     * Busca el carrito del cliente con sus ítems y productos, ordenados por producto
     * (mensajes [3] y [14] del diagrama de secuencia).
     *
     * @return el carrito, o {@code null} si el cliente no tiene carrito
     */
    @Query("""
            select c from Carrito c
            left join fetch c.items i
            left join fetch i.producto p
            where c.cliente.idUsuario = :idCliente
            order by p.idProducto
            """)
    Carrito findByClienteId(@Param("idCliente") Long idCliente);

    /** Guarda el carrito (mensaje [30]). */
    Carrito save(Carrito carrito);
}
