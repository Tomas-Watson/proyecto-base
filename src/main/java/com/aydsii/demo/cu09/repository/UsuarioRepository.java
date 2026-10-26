package com.aydsii.demo.cu09.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aydsii.demo.cu09.dominio.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}