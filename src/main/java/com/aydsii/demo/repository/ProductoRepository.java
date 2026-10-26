package com.aydsii.demo.repository;

import com.aydsii.demo.domain.Producto;
import org.springframework.stereotype.Repository;

/**
 * «Repository» ProductoRepository del diagrama de clases de diseño.
 */
@Repository
public interface ProductoRepository extends org.springframework.data.repository.Repository<Producto, Long> {

    /** Guarda el producto con su stock ya reservado (mensaje [20] del diagrama de secuencia). */
    Producto save(Producto producto);
}
