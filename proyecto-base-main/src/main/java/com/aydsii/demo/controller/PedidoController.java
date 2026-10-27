package com.aydsii.demo.controller;

import com.aydsii.demo.domain.Pedido;
import com.aydsii.demo.dto.ApiResponseDTO;
import com.aydsii.demo.dto.ErrorDTO;
import com.aydsii.demo.dto.EstadoPedidoDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * «Controller» PedidoController de los diagramas de clases de diseño de los dos casos de uso
 * (es una sola clase: cada diagrama muestra los métodos de su caso de uso):
 * <ul>
 *   <li>CU-05 Comprar:
 *       {@link #verResumenCompra} y {@link #confirmarCompra}.</li>
 *   <li>Consultar estado del pedido (CU-09): {@link #listarPedidos} y {@link #consultarEstado}.</li>
 * </ul>
 * La identificación (caso de uso Iniciar sesión) no forma parte de esta iteración: el cliente se
 * indica con su id en la ruta. Spring MVC valida {@code @Positive} sin necesidad de
 * {@code @Validated} en la clase.
 */
@RestController
@RequestMapping("/api")
@OpenAPIDefinition(info = @Info(title = "EcoGestión Verde · API", version = "1.0.0",
        description = PedidoController.DESCRIPCION_API),
        tags = {
                @Tag(name = PedidoController.TAG_COMPRAR, description = "Caso de uso Comprar (CU-05)."),
                @Tag(name = PedidoController.TAG_CONSULTAR, description = "Caso de uso Consultar estado del "
                        + "pedido (CU-09).")})
public class PedidoController {

    static final String TAG_COMPRAR = "1. Comprar (CU-05)";
    static final String TAG_CONSULTAR = "2. Consultar estado del pedido (CU-09)";

    static final String DESCRIPCION_API = """
            API del sistema **EcoGestión Verde** de VerdeGreen. Implementa los dos casos de uso de la primera \
            iteración según sus diagramas de clases de diseño y de secuencia.

            ## 1. Comprar (CU-05)
            Primero se consulta **GET /api/pedidos/resumen-compra/{idCliente}** (pasos 1 a 4) y después se \
            confirma con **POST /api/pedidos/confirmar-compra/{idCliente}** (pasos 5 a 8).

            | idCliente | Escenario |
            |---|---|
            | 1 | Camino feliz: hay stock de todo y se abona el 100 % |
            | 2 | FA-02: falta stock de productos que se fabrican a pedido, seña del 30 % |
            | 3 | FE-02: un producto ya no está publicado y se quita del detalle |
            | 4 | FE-02: ningún producto sigue publicado, el carrito queda vacío |
            | 5 | Carrito sin productos |
            | 6 | Cliente sin carrito |

            ## 2. Consultar estado del pedido (CU-09)
            **GET /api/cu09/usuarios/{idUsuario}/pedidos** lista los pedidos del cliente y \
            **GET /api/cu09/usuarios/{idUsuario}/pedidos/{idPedido}/estado** informa el estado de uno, con la \
            información que corresponde según el estado (FA-02 a FA-06). Las respuestas vienen dentro de \
            un ApiResponseDTO (`status`, `message`, `data`).

            | idUsuario | idPedido | Escenario |
            |---|---|---|
            | 6 | 1 | FA-02: Pendiente de pago (100 %), con el plazo para pagar |
            | 6 | 2 | FA-03: En cola de producción, se reembolsa la seña si cancela |
            | 6 | 3 | FA-03: En producción, la seña ya no se reembolsa |
            | 6 | 4 | FA-05: Cancelado, con la fecha de cancelación |
            | 6 | 5 | FA-06: Entregado, el envío se consulta en otro caso de uso |
            | 2 | 6 | Confirmado (pago completo) |
            | 6 | 6 | RN-01: el pedido es de otro cliente (403) |
            | 5 | – | FA-01: el cliente no tiene pedidos (lista vacía) |

            Los pedidos que se generan con **Comprar** también aparecen acá: por ejemplo, después de confirmar \
            la compra del cliente 1, **GET /api/cu09/usuarios/1/pedidos** devuelve el pedido nuevo en PENDIENTE_DE_PAGO.
            """;

    private static final String DESCRIPCION_ID_CLIENTE = "Identificador del cliente que realiza la compra";
    private static final String DESCRIPCION_ID_USUARIO = "Identificador del cliente que consulta sus pedidos";

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // =====================================================================================
    // CU-05 Comprar
    // =====================================================================================

    @GetMapping(value = "/pedidos/resumen-compra/{idCliente}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            tags = TAG_COMPRAR,
            operationId = "verResumenCompra",
            summary = "Pasos 1 a 4: ver el resumen de la compra",
            description = """
                    Mensajes [1] a [11] del diagrama de secuencia de CU-05.

                    Presenta el detalle de los productos del carrito (nombre, precio unitario vigente, cantidad y \
                    subtotal) y el total general (RN-01, RN-02). Verifica el stock: si alcanza para todo, el importe \
                    a abonar es el 100 % del total; si falta stock de algún producto, es una seña del 30 % (FA-02, \
                    RN-03). Los productos con `stockDisponible` menor a la cantidad son los que no tienen stock \
                    suficiente, y `tiempoEstimadoProduccion` indica los días de fabricación de los que se hacen a pedido.

                    Los productos que ya no están publicados se quitan del detalle y se recalcula el total (FE-02). \
                    Esta consulta **no modifica** el carrito ni el stock: si el cliente no confirma, el carrito \
                    conserva sus productos (FA-03).""")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Resumen de la compra",
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
        return pedidoService.verResumenCompra(idCliente);                                 
    }

    @PostMapping(value = "/pedidos/confirmar-compra/{idCliente}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            tags = TAG_COMPRAR,
            operationId = "confirmarCompra",
            summary = "Pasos 5 a 8: confirmar la compra",
            description = """
                    Mensajes [12] a [32] del diagrama de secuencia de CU-05.

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
        return pedidoService.confirmarCompra(idCliente);                                  
    }

    // =====================================================================================
    // CU-09 Consultar estado del pedido
    // =====================================================================================

    @GetMapping(value = "/cu09/usuarios/{idUsuario}/pedidos", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            tags = TAG_CONSULTAR,
            operationId = "listarPedidos",
            summary = "Parte 1: listar los pedidos del cliente",
            description = """
                    Mensajes [1] a [12] del diagrama de secuencia de CU-09.

                    Presenta los pedidos del cliente con identificador, fecha y hora, monto total y estado. \
                    Si el cliente no tiene pedidos, `data` viene vacío y `message` lo informa (FA-01).""")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedidos del cliente (o lista vacía, FA-01)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "El idUsuario no es un número entero positivo",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<ApiResponseDTO<List<Pedido>>> listarPedidos(
            @Parameter(description = DESCRIPCION_ID_USUARIO, example = "6", required = true)
            @PathVariable("idUsuario") @Positive Long idUsuario) {
        List<Pedido> pedidos = pedidoService.listarPedidos(idUsuario);                    // [3] a [6]
        String mensaje = pedidos.isEmpty()
                ? "El cliente no tiene pedidos registrados (FA-01)."
                : "Pedidos recuperados con éxito";
        ApiResponseDTO<List<Pedido>> apiResponse = new ApiResponseDTO<>(200, mensaje, pedidos); // [7] [8]
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);                    // [9] a [11]
    }

    @GetMapping(value = "/cu09/usuarios/{idUsuario}/pedidos/{idPedido}/estado",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            tags = TAG_CONSULTAR,
            operationId = "consultarEstado",
            summary = "Parte 2: consultar el estado de un pedido",
            description = """
                    Mensajes [13] a [26] del diagrama de secuencia de CU-09.

                    Verifica que el pedido pertenezca al cliente (RN-01) y devuelve su estado vigente con sus datos \
                    principales. En `informacionComplementaria` agrega lo que corresponde según el estado:
                    - **FA-02** Pendiente de pago: importe a abonar (100 % o seña del 30 %) y plazo para pagar.
                    - **FA-03** En cola de producción / En producción: tiempo estimado de producción, saldo del 70 % \
                    y si se reembolsaría la seña al cancelar (RN-04).
                    - **FA-05** Cancelado: fecha de cancelación.
                    - **FA-06** Listo para despacho, Despachado, En tránsito o Entregado: remite a «Consultar \
                    estado del envío».""")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado vigente del pedido",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = {
                                    @ExampleObject(name = "En cola de producción (6/2)", summary = "FA-03",
                                            value = EjemplosSwagger.ESTADO_EN_COLA)})),
            @ApiResponse(responseCode = "400", description = "Algún id no es un número entero positivo",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "403", description = "El pedido no es del cliente (RN-01)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorDTO.class),
                            examples = @ExampleObject(name = "Pedido de otro cliente (6/6)", summary = "RN-01",
                                    value = EjemplosSwagger.ERROR_PEDIDO_AJENO))),
            @ApiResponse(responseCode = "404", description = "El pedido no existe",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<ApiResponseDTO<EstadoPedidoDTO>> consultarEstado(
            @Parameter(description = DESCRIPCION_ID_USUARIO, example = "6", required = true)
            @PathVariable("idUsuario") @Positive Long idUsuario,
            @Parameter(description = "Número del pedido a consultar", example = "2", required = true)
            @PathVariable("idPedido") @Positive Long idPedido) {
        EstadoPedidoDTO dto = pedidoService.consultarEstado(idPedido, idUsuario);         // [15] a [20]
        ApiResponseDTO<EstadoPedidoDTO> apiResponse =
                new ApiResponseDTO<>(200, "Estado consultado con éxito", dto);            // [21] [22]
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);                    // [23] a [25]
    }
}
