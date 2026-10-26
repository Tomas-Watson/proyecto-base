package com.aydsii.demo.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * «Entity» Carrito del diagrama de clases de diseño (tabla {@code carrito} del DER).
 * <p>
 * Un cliente tiene 0..1 carrito y el carrito contiene 0..* ítems (composición): al vaciarlo,
 * los ítems se eliminan de la base ({@code orphanRemoval}).
 */
@Entity
@Table(name = "carrito")
@Schema(description = "Carrito de compras del cliente")
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrito")
    private Long idCarrito;

    @OneToOne(optional = false)
    @JoinColumn(name = "id_cliente", nullable = false, unique = true)
    private Cliente cliente;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCarrito> items = new ArrayList<>();

    /** Constructor requerido por JPA. */
    protected Carrito() {
    }

    public Carrito(Long idCarrito, Cliente cliente) {
        this.idCarrito = idCarrito;
        this.cliente = cliente;
    }

    /** Indica si el carrito no tiene productos. */
    public Boolean estaVacio() {
        return items.isEmpty();
    }

    /** Quita todos los productos del carrito (mensaje [29] del diagrama de secuencia). */
    public void vaciar() {
        items.clear();
    }

    public Long getIdCarrito() {
        return idCarrito;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public List<ItemCarrito> getItems() {
        return items;
    }
}
