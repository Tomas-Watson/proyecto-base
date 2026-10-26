package com.aydsii.demo.cu09.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aydsii.demo.cu09.dominio.Pedido;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteIdUsuario(Long idUsuario);
}