package com.aydsii.demo.repository;

import com.aydsii.demo.domain.Carrito;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface CarritoRepository extends org.springframework.data.repository.Repository<Carrito, Long> {

   
    @Query("""
            select c from Carrito c
            left join fetch c.items i
            left join fetch i.producto p
            where c.cliente.idUsuario = :idCliente
            order by p.idProducto
            """)
    Carrito findByClienteId(@Param("idCliente") Long idCliente);

   
    Carrito save(Carrito carrito);
}
