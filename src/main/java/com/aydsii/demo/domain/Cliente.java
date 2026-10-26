package com.aydsii.demo.domain;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.Table;

/**
 * «Entity» Cliente del diagrama de clases de diseño del caso de uso Comprar.
 * <p>
 * En el DER un cliente es un usuario: {@code cliente.id_cliente} es a la vez clave primaria y
 * clave foránea a {@code usuario.id_usuario}. Por eso el atributo {@code idUsuario} del diagrama
 * se mapea a {@code cliente.id_cliente}, y {@code nombre} y {@code email} se leen de la tabla
 * {@code usuario}, que se une como tabla secundaria.
 * <p>
 * En el DER la clave foránea va de {@code cliente} a {@code usuario}. {@code NO_CONSTRAINT} evita
 * que Hibernate cree otra en sentido contrario si se usa {@code spring.jpa.hibernate.ddl-auto=update}.
 */
@Entity
@Table(name = "cliente")
@SecondaryTable(name = "usuario",
        pkJoinColumns = @PrimaryKeyJoinColumn(name = "id_usuario", referencedColumnName = "id_cliente"),
        foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
@JsonPropertyOrder({"idUsuario", "nombre", "email"})
@Schema(description = "Cliente que realiza el pedido")
public class Cliente {

    @Id
    @Column(name = "id_cliente")
    @Schema(description = "Identificador del cliente (coincide con su id de usuario)", example = "1")
    private Long idUsuario;

    @Column(name = "nombre", table = "usuario", nullable = false, length = 100)
    @Schema(description = "Nombre completo", example = "Ana Gómez")
    private String nombre;

    @Column(name = "email", table = "usuario", nullable = false, length = 120)
    @Schema(description = "Email con el que inicia sesión", example = "ana@mail.test")
    private String email;

    /** Constructor requerido por JPA. */
    protected Cliente() {
    }

    public Cliente(Long idUsuario, String nombre, String email) {
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
