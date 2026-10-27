package com.aydsii.demo.domain;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

/**
 * «Entity» Usuario del diagrama de clases de diseño del caso de uso Consultar estado del pedido
 * ({usuario} del DER): el pedido se asocia al usuario que lo realizó (rol «cliente»).
 *
 * En el DER un cliente es un usuario: {cliente.id_cliente} es a la vez clave primaria y clave
 * foránea a {usuario.id_usuario}. Por eso {@link Cliente} hereda de Usuario y la herencia se
 * mapea con {@link InheritanceType#JOINED}. La columna {tipo_usuario} indica el tipo de cada fila.
 * Los empleados no participan en estos casos de uso, así que no se mapean.
 */
@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_usuario", discriminatorType = DiscriminatorType.STRING)
@JsonPropertyOrder({"idUsuario", "nombre", "email"})
@Schema(description = "Usuario del sistema")
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    @Schema(description = "Identificador del usuario", example = "1")
    private Long idUsuario;

    @Column(name = "nombre", nullable = false, length = 100)
    @Schema(description = "Nombre completo", example = "Ana Gómez")
    private String nombre;

    @Column(name = "email", nullable = false, length = 120)
    @Schema(description = "Email con el que inicia sesión", example = "ana@mail.test")
    private String email;

    /** Constructor requerido por JPA. */
    protected Usuario() {
    }

    protected Usuario(Long idUsuario, String nombre, String email) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.email = email;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }
}
