package com.aydsii.demo.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

/**
 * «Entity» Cliente del diagrama de clases de diseño de Comprar ({cliente} del DER). En el de
 * Consultar estado del pedido aparece como el {@link Usuario} que hizo el pedido.
 *
 * Es un {@link Usuario}: hereda {idUsuario}, {nombre} y {email}. En el DER
 * {cliente.id_cliente} es a la vez clave primaria y clave foránea a {usuario.id_usuario}.
 * Un cliente tiene 0..1 carrito y realiza 0..* pedidos (esas asociaciones se navegan desde
 * {@link Carrito} y {@link Pedido}).
 */
@Entity
@Table(name = "cliente")
@PrimaryKeyJoinColumn(name = "id_cliente")
@DiscriminatorValue("CLIENTE")
@Schema(description = "Cliente que realiza el pedido")
public class Cliente extends Usuario {

    /** Constructor requerido por JPA. */
    protected Cliente() {
    }

    public Cliente(Long idUsuario, String nombre, String email) {
        super(idUsuario, nombre, email);
    }
}
