package com.aydsii.demo.service;

import java.io.Serial;
import java.util.List;

/**
 * El cliente no tiene productos para comprar: no tiene carrito, el carrito está vacío o ninguno de
 * sus productos sigue publicado (FE-02). La compra no se registra y el carrito queda como estaba.
 */
public class CarritoVacioException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Mensaje del FE-02 cuando ningún producto del carrito sigue publicado. */
    public static final String NINGUN_PRODUCTO_PUBLICADO = "Los productos del carrito ya no están publicados en el "
            + "catálogo y se quitaron del detalle, por lo que el carrito quedó vacío (FE-02).";

    private final List<String> productosNoDisponibles;

    private CarritoVacioException(String mensaje, List<String> productosNoDisponibles) {
        super(mensaje);
        this.productosNoDisponibles = List.copyOf(productosNoDisponibles);
    }

    /** El cliente no tiene carrito o su carrito no tiene productos. */
    public static CarritoVacioException sinProductos(Long idCliente) {
        return new CarritoVacioException("El cliente " + idCliente + " no tiene productos en el carrito.", List.of());
    }

    /** FE-02: ninguno de los productos del carrito sigue publicado en el catálogo. */
    public static CarritoVacioException ningunoPublicado(List<String> productosNoDisponibles) {
        return new CarritoVacioException(NINGUN_PRODUCTO_PUBLICADO, productosNoDisponibles);
    }

    /** Nombres de los productos que se quitaron del detalle por no estar publicados (FE-02). */
    public List<String> getProductosNoDisponibles() {
        return productosNoDisponibles;
    }
}
