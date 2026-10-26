package com.aydsii.demo.controller;

import com.aydsii.demo.domain.Pedido;
import com.aydsii.demo.dto.ErrorDTO;
import com.aydsii.demo.dto.ResumenCompraDTO;
import com.aydsii.demo.service.PedidoService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * «Controller» PedidoController del diagrama de clases de diseño del caso de uso Comprar
 * (CU-01 en los diagramas y la especificación, CU-05 en el README de casos de uso).
 * <p>
 * Los dos métodos reciben el {@code idCliente}, como en el diagrama. Spring MVC valida
 * {@code @Positive} sin necesidad de {@code @Validated} en la clase. No reciben cuerpo, por eso no
 * llevan {@code @RequestBody}.
 */
@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Comprar (CU-01)",
        description = "Caso de uso Comprar: CU-01 en los diagramas y la especificación, CU-05 en el README de "
                + "casos de uso. La identificación del cliente (caso de uso Iniciar sesión) no forma parte de "
                + "esta implementación: el cliente se indica con idCliente.")
@OpenAPIDefinition(info = @Info(title = "EcoGestión Verde · API", version = "1.0.0",
        description = PedidoController.DESCRIPCION_API))
public class PedidoController {

    static final String DESCRIPCION_API = """
            API del sistema **EcoGestión Verde** de VerdeGreen.

            Implementa el caso de uso **Comprar** (CU-01 en los diagramas, CU-05 en el README de casos de uso) \
            según el diagrama de clases de diseño y el diagrama de secuencia.

            Primero se consulta **GET /api/pedidos/resumen-compra/{idCliente}** (pasos 1 a 3) y después se \
            confirma con **POST /api/pedidos/confirmar-compra/{idCliente}** (pasos 4 a 7).

            ### Clientes de los datos de prueba
            | idCliente | Escenario |
            |---|---|
            | 1 | Camino feliz: hay stock de todo y se abona el 100 % |
            | 2 | FA-02: falta stock de productos que se fabrican a pedido, seña del 30 % |
            | 3 | FE-02: un producto ya no está publicado y se quita del detalle |
            | 4 | FE-02: ningún producto sigue publicado, el carrito queda vacío |
            | 5 | Carrito sin productos |
            | 6 | Cliente sin carrito |
            """;

    private static final String DESCRIPCION_ID_CLIENTE = "Identificador del cliente que realiza la compra";

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping(value = "/resumen-compra/{idCliente}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            operationId = "verResumenCompra",
            summary = "Pasos 1 a 3: ver el resumen de la compra",
            description = """
                    Mensajes [1] a [11] del diagrama de secuencia.

                    Presenta el detalle de los productos del carrito (nombre, precio unitario vigente, cantidad y \
                    subtotal) y el total general (RN-01, RN-02). Verifica el stock: si alcanza para todo, el importe \
                    a abonar es el 100 % del total; si falta stock de algún producto, es una seña del 30 % (FA-02, \
                    RN-03). Los productos con `stockDisponible` menor a la cantidad son los que no tienen stock \
                    suficiente, y `tiempoEstimadoProduccion` indica los días de fabricación de los que se hacen a pedido.

                    Los productos que ya no están publicados se quitan del detalle y se recalcula el total (FE-02). \
                    Esta consulta **no modifica** el carrito ni el stock: si el cliente no confirma, el carrito \
                    conserva sus productos (FA-03).""")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resumen de la compra",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResumenCompraDTO.class),
                            examples = {
                                    @ExampleObject(name = "Hay stock de todo (cliente 1)",
                                            summary = "Camino feliz: se abona el 100 %",
                                            value = EjemplosSwagger.RESUMEN_CON_STOCK),
                                    @ExampleObject(name = "Falta stock (cliente 2)",
                                            summary = "FA-02: seña del 30 %",
                                            value = EjemplosSwagger.RESUMEN_SIN_STOCK),
                                    @ExampleObject(name = "Producto despublicado (cliente 3)",
                                            summary = "FE-02: se quitó un producto y se recalculó el total",
                                            value = EjemplosSwagger.RESUMEN_PRODUCTO_DESPUBLICADO)})),
            @ApiResponse(responseCode = "400", description = "El idCliente no es un número entero positivo",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorDTO.class),
                            examples = @ExampleObject(value = EjemplosSwagger.ERROR_ID_CLIENTE))),
            @ApiResponse(responseCode = "409", description = "El cliente no tiene productos para comprar",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorDTO.class),
                            examples = {
                                    @ExampleObject(name = "Carrito vacío (cliente 5)",
                                            value = EjemplosSwagger.ERROR_CARRITO_VACIO),
                                    @ExampleObject(name = "Ningún producto publicado (cliente 4)",
                                            summary = "FE-02: finaliza el caso de uso",
                                            value = EjemplosSwagger.ERROR_NINGUNO_PUBLICADO)})),
            @ApiResponse(responseCode = "500", description = "Error inesperado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorDTO.class),
                            examples = @ExampleObject(value = EjemplosSwagger.ERROR_INTERNO)))
    })
    public ResumenCompraDTO verResumenCompra(
            @Parameter(description = DESCRIPCION_ID_CLIENTE, example = "1", required = true)
            @PathVariable("idCliente") @Positive Long idCliente) {
        return pedidoService.verResumenCompra(idCliente);                                 // [1] [2] ... [10] [11]
    }

    @PostMapping(value = "/confirmar-compra/{idCliente}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            operationId = "confirmarCompra",
            summary = "Pasos 4 a 7: confirmar la compra",
            description = """
                    Mensajes [12] a [32] del diagrama de secuencia.

                    Genera el pedido en estado PENDIENTE_DE_PAGO con el cliente, la fecha y hora, el detalle, el monto \
                    total y la cantidad de productos. Reserva el stock por 24 horas (RN-04) y vacía el carrito. Usa los \
                    precios vigentes al confirmar (RN-02). Si falta stock, reserva solo lo disponible y el importe a \
                    abonar es la seña del 30 % (FA-02).

                    La respuesta informa el número de pedido (`idPedido`), el importe a abonar (`importeAAbonar`) y el \
                    plazo para pagar (`vencimientoReserva`). Si algo falla, no se registra el pedido, no se reserva \
                    stock y el carrito queda como estaba.""")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido registrado en estado PENDIENTE_DE_PAGO",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Pedido.class),
                            examples = {
                                    @ExampleObject(name = "Hay stock de todo (cliente 1)",
                                            summary = "Camino feliz: se abona el 100 %",
                                            value = EjemplosSwagger.PEDIDO_CON_STOCK),
                                    @ExampleObject(name = "Falta stock (cliente 2)",
                                            summary = "FA-02: se reserva solo lo disponible y se abona la seña",
                                            value = EjemplosSwagger.PEDIDO_SIN_STOCK)})),
            @ApiResponse(responseCode = "400", description = "El idCliente no es un número entero positivo",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorDTO.class),
                            examples = @ExampleObject(value = EjemplosSwagger.ERROR_ID_CLIENTE))),
            @ApiResponse(responseCode = "409", description = "No hay productos para comprar, o el stock cambió "
                    + "mientras se confirmaba",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorDTO.class),
                            examples = {
                                    @ExampleObject(name = "Carrito vacío",
                                            summary = "También ocurre si se confirma dos veces seguidas",
                                            value = EjemplosSwagger.ERROR_CARRITO_VACIO),
                                    @ExampleObject(name = "Ningún producto publicado (cliente 4)",
                                            value = EjemplosSwagger.ERROR_NINGUNO_PUBLICADO),
                                    @ExampleObject(name = "Conflicto de stock",
                                            summary = "Otro cliente reservó el mismo producto al mismo tiempo",
                                            value = EjemplosSwagger.ERROR_CONFLICTO_STOCK)})),
            @ApiResponse(responseCode = "500", description = "Error inesperado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorDTO.class),
                            examples = @ExampleObject(value = EjemplosSwagger.ERROR_INTERNO)))
    })
    public Pedido confirmarCompra(
            @Parameter(description = DESCRIPCION_ID_CLIENTE, example = "1", required = true)
            @PathVariable("idCliente") @Positive Long idCliente) {
        return pedidoService.confirmarCompra(idCliente);                                  // [12] [13] ... [31] [32]
    }
}
