package com.aydsii.demo.controller;

import com.aydsii.demo.service.CarritoVacioException;

/**
 * Ejemplos de respuestas que muestra Swagger UI para los endpoints de {@link PedidoController}.
 * Coinciden con los datos de prueba de {@code data.sql}, y los errores reutilizan los textos del
 * manejador de excepciones.
 */
final class EjemplosSwagger {

    private static final String FECHA = "\"fecha\": \"2026-10-25T10:30:00\"";

    // ------------------------------------------------------------------ Resumen de la compra

    static final String RESUMEN_CON_STOCK = "{\"items\": ["
            + "{\"producto\": {\"idProducto\": 1, \"nombre\": \"Maceta mediana de PET reciclado\", \"precio\": 4500.00, "
            + "\"publicado\": true, \"stockDisponible\": 50, \"tiempoEstimadoProduccion\": null}, "
            + "\"cantidad\": 2, \"subtotal\": 9000.00}, "
            + "{\"producto\": {\"idProducto\": 2, \"nombre\": \"Bandeja de PET reciclado\", \"precio\": 2800.00, "
            + "\"publicado\": true, \"stockDisponible\": 120, \"tiempoEstimadoProduccion\": null}, "
            + "\"cantidad\": 3, \"subtotal\": 8400.00}, "
            + "{\"producto\": {\"idProducto\": 3, \"nombre\": \"Botella reutilizable de 1 L\", \"precio\": 3200.00, "
            + "\"publicado\": true, \"stockDisponible\": 80, \"tiempoEstimadoProduccion\": null}, "
            + "\"cantidad\": 1, \"subtotal\": 3200.00}], "
            + "\"montoTotal\": 20600.00, \"importeAAbonar\": 20600.00, \"hayStockSuficiente\": true, "
            + "\"plazoReservaHoras\": 24}";

    static final String RESUMEN_SIN_STOCK = "{\"items\": ["
            + "{\"producto\": {\"idProducto\": 1, \"nombre\": \"Maceta mediana de PET reciclado\", \"precio\": 4500.00, "
            + "\"publicado\": true, \"stockDisponible\": 50, \"tiempoEstimadoProduccion\": null}, "
            + "\"cantidad\": 1, \"subtotal\": 4500.00}, "
            + "{\"producto\": {\"idProducto\": 5, \"nombre\": \"Banco de plaza de PET\", \"precio\": 185000.00, "
            + "\"publicado\": true, \"stockDisponible\": 2, \"tiempoEstimadoProduccion\": 10}, "
            + "\"cantidad\": 3, \"subtotal\": 555000.00}, "
            + "{\"producto\": {\"idProducto\": 6, \"nombre\": \"Mesa de exterior de PET\", \"precio\": 320000.00, "
            + "\"publicado\": true, \"stockDisponible\": 0, \"tiempoEstimadoProduccion\": 15}, "
            + "\"cantidad\": 1, \"subtotal\": 320000.00}], "
            + "\"montoTotal\": 879500.00, \"importeAAbonar\": 263850.00, \"hayStockSuficiente\": false, "
            + "\"plazoReservaHoras\": 24}";

    static final String RESUMEN_PRODUCTO_DESPUBLICADO = "{\"items\": ["
            + "{\"producto\": {\"idProducto\": 4, \"nombre\": \"Set de 6 platos reciclados\", \"precio\": 5600.00, "
            + "\"publicado\": true, \"stockDisponible\": 40, \"tiempoEstimadoProduccion\": null}, "
            + "\"cantidad\": 2, \"subtotal\": 11200.00}], "
            + "\"montoTotal\": 11200.00, \"importeAAbonar\": 11200.00, \"hayStockSuficiente\": true, "
            + "\"plazoReservaHoras\": 24}";

    // ------------------------------------------------------------------ Pedido confirmado

    static final String PEDIDO_CON_STOCK = "{\"idPedido\": 1, \"estado\": \"PENDIENTE_DE_PAGO\", "
            + "\"fechaHora\": \"2026-10-25T10:30:00\", \"vencimientoReserva\": \"2026-10-26T10:30:00\", "
            + "\"cliente\": {\"idUsuario\": 1, \"nombre\": \"Ana Gómez\", \"email\": \"ana@mail.test\"}, "
            + "\"detalles\": ["
            + "{\"producto\": {\"idProducto\": 1, \"nombre\": \"Maceta mediana de PET reciclado\", \"precio\": 4500.00, "
            + "\"publicado\": true, \"stockDisponible\": 48, \"tiempoEstimadoProduccion\": null}, "
            + "\"cantidad\": 2, \"precioUnitario\": 4500.00, \"subtotal\": 9000.00, \"cantidadReservada\": 2}, "
            + "{\"producto\": {\"idProducto\": 2, \"nombre\": \"Bandeja de PET reciclado\", \"precio\": 2800.00, "
            + "\"publicado\": true, \"stockDisponible\": 117, \"tiempoEstimadoProduccion\": null}, "
            + "\"cantidad\": 3, \"precioUnitario\": 2800.00, \"subtotal\": 8400.00, \"cantidadReservada\": 3}, "
            + "{\"producto\": {\"idProducto\": 3, \"nombre\": \"Botella reutilizable de 1 L\", \"precio\": 3200.00, "
            + "\"publicado\": true, \"stockDisponible\": 79, \"tiempoEstimadoProduccion\": null}, "
            + "\"cantidad\": 1, \"precioUnitario\": 3200.00, \"subtotal\": 3200.00, \"cantidadReservada\": 1}], "
            + "\"cantidadProductos\": 6, \"montoTotal\": 20600.00, \"importeAAbonar\": 20600.00}";

    static final String PEDIDO_SIN_STOCK = "{\"idPedido\": 1, \"estado\": \"PENDIENTE_DE_PAGO\", "
            + "\"fechaHora\": \"2026-10-25T10:30:00\", \"vencimientoReserva\": \"2026-10-26T10:30:00\", "
            + "\"cliente\": {\"idUsuario\": 2, \"nombre\": \"Bruno Díaz\", \"email\": \"bruno@mail.test\"}, "
            + "\"detalles\": ["
            + "{\"producto\": {\"idProducto\": 1, \"nombre\": \"Maceta mediana de PET reciclado\", \"precio\": 4500.00, "
            + "\"publicado\": true, \"stockDisponible\": 49, \"tiempoEstimadoProduccion\": null}, "
            + "\"cantidad\": 1, \"precioUnitario\": 4500.00, \"subtotal\": 4500.00, \"cantidadReservada\": 1}, "
            + "{\"producto\": {\"idProducto\": 5, \"nombre\": \"Banco de plaza de PET\", \"precio\": 185000.00, "
            + "\"publicado\": true, \"stockDisponible\": 0, \"tiempoEstimadoProduccion\": 10}, "
            + "\"cantidad\": 3, \"precioUnitario\": 185000.00, \"subtotal\": 555000.00, \"cantidadReservada\": 2}, "
            + "{\"producto\": {\"idProducto\": 6, \"nombre\": \"Mesa de exterior de PET\", \"precio\": 320000.00, "
            + "\"publicado\": true, \"stockDisponible\": 0, \"tiempoEstimadoProduccion\": 15}, "
            + "\"cantidad\": 1, \"precioUnitario\": 320000.00, \"subtotal\": 320000.00, \"cantidadReservada\": 0}], "
            + "\"cantidadProductos\": 5, \"montoTotal\": 879500.00, \"importeAAbonar\": 263850.00}";

    // ------------------------------------------------------------------ Errores del caso de uso

    static final String ERROR_ID_CLIENTE = "{\"status\": 400, \"titulo\": \""
            + ManejadorGlobalExcepciones.TITULO_SOLICITUD_INVALIDA + "\", \"mensaje\": \"El parámetro 'idCliente' debe ser un "
            + "número entero positivo (valor recibido: 'abc').\", \"detalles\": [], "
            + "\"ruta\": \"/api/pedidos/resumen-compra/abc\", " + FECHA + "}";

    static final String ERROR_CARRITO_VACIO = "{\"status\": 409, \"titulo\": \""
            + ManejadorGlobalExcepciones.TITULO_CARRITO_VACIO + "\", \"mensaje\": \"El cliente 5 no tiene productos en el carrito.\", "
            + "\"detalles\": [], \"ruta\": \"/api/pedidos/resumen-compra/5\", " + FECHA + "}";

    static final String ERROR_NINGUNO_PUBLICADO = "{\"status\": 409, \"titulo\": \""
            + ManejadorGlobalExcepciones.TITULO_CARRITO_VACIO + "\", \"mensaje\": \"" + CarritoVacioException.NINGUN_PRODUCTO_PUBLICADO
            + "\", \"detalles\": [\"Juego infantil con tobogán\", \"Maceta colgante (discontinuada)\"], "
            + "\"ruta\": \"/api/pedidos/resumen-compra/4\", " + FECHA + "}";

    static final String ERROR_CONFLICTO_STOCK = "{\"status\": 409, \"titulo\": \""
            + ManejadorGlobalExcepciones.TITULO_CONFLICTO_STOCK + "\", \"mensaje\": \"" + ManejadorGlobalExcepciones.CONFLICTO_STOCK + "\", "
            + "\"detalles\": [], \"ruta\": \"/api/pedidos/confirmar-compra/1\", " + FECHA + "}";

    static final String ERROR_INTERNO = "{\"status\": 500, \"titulo\": \"" + ManejadorGlobalExcepciones.TITULO_ERROR_INTERNO
            + "\", \"mensaje\": \"" + ManejadorGlobalExcepciones.ERROR_INTERNO + "\", \"detalles\": [], "
            + "\"ruta\": \"/api/pedidos/confirmar-compra/1\", " + FECHA + "}";

    private EjemplosSwagger() {
    }
}
