package com.aydsii.demo.repository;

import com.aydsii.demo.domain.Producto;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductoRepository extends org.springframework.data.repository.Repository<Producto, Long> {

    
    Producto save(Producto producto);
}
