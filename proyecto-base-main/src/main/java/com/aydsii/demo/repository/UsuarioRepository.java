package com.aydsii.demo.repository;

import com.aydsii.demo.domain.Usuario;
import org.springframework.stereotype.Repository;

/**
 * «Repository» UsuarioRepository del diagrama de clases de diseño de CU-09 Consultar estado del
 * pedido. En el diagrama no tiene métodos y el diagrama de secuencia no le envía mensajes, así que
 * extiende {@link org.springframework.data.repository.Repository} sin declarar ninguno.
 */
@Repository
public interface UsuarioRepository extends org.springframework.data.repository.Repository<Usuario, Long> {
}
