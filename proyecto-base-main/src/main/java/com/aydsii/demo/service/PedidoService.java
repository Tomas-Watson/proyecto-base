package com.aydsii.demo.service;

import com.aydsii.demo.domain.Carrito;
import com.aydsii.demo.domain.DetallePedido;
import com.aydsii.demo.domain.EstadoPedido;
import com.aydsii.demo.domain.ItemCarrito;
import com.aydsii.demo.domain.Pedido;
import com.aydsii.demo.domain.Producto;
import com.aydsii.demo.dto.EstadoPedidoDTO;
import com.aydsii.demo.dto.ResumenCompraDTO;
import com.aydsii.demo.repository.CarritoRepository;
import com.aydsii.demo.repository.PedidoRepository;
import com.aydsii.demo.repository.ProductoRepository;
import com.aydsii.demo.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * «Service» PedidoService de los diagramas de clases de diseño de los dos casos de uso:
 * Los números entre corchetes en los comentarios son los mensajes del diagrama de secuencia de
 * cada caso de uso.
 */
@Service
public class PedidoService {

    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Asociaciones del diagrama de CU-05
    private final CarritoRepository carritoRepository;
    private final ProductoRepository productoRepository;
    // Asociación de los dos diagramas
    private final PedidoRepository pedidoRepository;
    // Asociación del diagrama de CU-09
    private final UsuarioRepository usuarioRepository;

    public PedidoService(CarritoRepository carritoRepository,
                         ProductoRepository productoRepository,
                         PedidoRepository pedidoRepository,
                         UsuarioRepository usuarioRepository) {
        this.carritoRepository = carritoRepository;
        this.productoRepository = productoRepository;
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // =====================================================================================
    // CU-05 Comprar
    // =====================================================================================

    /**
     * Pasos 1 a 4 (mensajes [2] a [10]): arma el detalle de la compra, calcula el total, verifica
     * el stock e informa el importe a abonar.
     * La transacción es de solo lectura: quitar del detalle los productos despublicados (FE-02) no
     * modifica el carrito guardado, así que si el cliente no confirma, el carrito conserva sus
     * productos (FA-03).
     *
     * @throws CarritoVacioException si el cliente no tiene productos para comprar
     */
    @Transactional(readOnly = true)
    public ResumenCompraDTO verResumenCompra(Long idCliente) {
        Carrito carrito = carritoRepository.findByClienteId(idCliente);                   // [3] [4]
        if (carrito == null || carrito.estaVacio()) {
            throw CarritoVacioException.sinProductos(idCliente);
        }
        quitarNoPublicados(carrito);                                                      // FE-02

        BigDecimal montoTotal = BigDecimal.ZERO;
        boolean hayStockSuficiente = true;
        for (ItemCarrito item : carrito.getItems()) {                                     // loop
            montoTotal = montoTotal.add(item.calcularSubtotal());                         // [5] [6]
            if (!item.getProducto().hayStock(item.getCantidad())) {                       // [7] [8]
                hayStockSuficiente = false;                                               // FA-02
            }
        }
        montoTotal = montoTotal.setScale(2, RoundingMode.HALF_UP);
        BigDecimal importeAAbonar = hayStockSuficiente
                ? montoTotal
                : montoTotal.multiply(Pedido.PORCENTAJE_SENA).setScale(2, RoundingMode.HALF_UP);

        return new ResumenCompraDTO(new ArrayList<>(carrito.getItems()), montoTotal,     // [9] [10]
                importeAAbonar, hayStockSuficiente, Pedido.PLAZO_RESERVA_HORAS);
    }

    /**
     * Pasos 5 a 8 (mensajes [13] a [31]): genera el pedido en «Pendiente de pago», reserva el
     * stock por 24 horas y vacía el carrito.
     * Todo ocurre en una sola transacción: si algo falla, no se registra el pedido, no se reserva
     * stock y el carrito conserva sus productos (postcondición de fallo).
     *
     * @throws CarritoVacioException si el cliente no tiene productos para comprar
     */
    @Transactional
    public Pedido confirmarCompra(Long idCliente) {
        Carrito carrito = carritoRepository.findByClienteId(idCliente);                   // [14] [15]
        if (carrito == null || carrito.estaVacio()) {
            throw CarritoVacioException.sinProductos(idCliente);
        }
        quitarNoPublicados(carrito);                                                      // FE-02

        Pedido pedido = generarPedido(carrito);                                           // [16]
        Pedido pedidoGuardado = pedidoRepository.save(pedido);                            // [27] [28]
        carrito.vaciar();                                                                 // [29]
        carritoRepository.save(carrito);                                                  // [30]

        log.info("Pedido {} registrado para el cliente {}. Importe a abonar: {}",
                pedidoGuardado.getIdPedido(), idCliente, pedidoGuardado.getImporteAAbonar());
        return pedidoGuardado;                                                            // [31]
    }

    /**
     * FE-02: quita del detalle los productos que ya no están publicados en el catálogo. Si no
     * queda ninguno, informa que el carrito está vacío y la compra no continúa.
     */
    private void quitarNoPublicados(Carrito carrito) {
        List<String> quitados = new ArrayList<>();
        Iterator<ItemCarrito> iterador = carrito.getItems().iterator();
        while (iterador.hasNext()) {
            Producto producto = iterador.next().getProducto();
            if (!Boolean.TRUE.equals(producto.getPublicado())) {
                quitados.add(producto.getNombre());
                iterador.remove();
            }
        }
        if (quitados.isEmpty()) {
            return;
        }
        if (carrito.estaVacio()) {
            throw CarritoVacioException.ningunoPublicado(quitados);
        }
        log.info("FE-02: se quitaron del detalle productos que ya no están publicados: {}", quitados);
    }

    /**
     * Mensajes [16] a [26]: crea el pedido, reserva el stock de cada producto y calcula el monto
     * total y el importe a abonar. Si no alcanza el stock de algún producto, reserva solo lo
     * disponible y el importe es la seña del 30 % (FA-02). Se usan los precios vigentes al
     * confirmar (RN-02).
     */
    private Pedido generarPedido(Carrito carrito) {
        Pedido pedido = new Pedido(carrito.getCliente(),
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));                     // [17]
        boolean hayStock = true;
        for (ItemCarrito item : carrito.getItems()) {                                     // loop
            Producto producto = item.getProducto();
            Integer cantidadReservada = producto.reservar(item.getCantidad());            // [18] [19]
            productoRepository.save(producto);                                            // [20]
            DetallePedido detalle = new DetallePedido(producto, item.getCantidad(),
                    producto.getPrecio(), cantidadReservada);                             // [21]
            pedido.agregarDetalle(detalle);                                               // [22]
            if (cantidadReservada < item.getCantidad()) {
                hayStock = false;                                                         // FA-02
            }
        }
        pedido.calcularMontoTotal();                                                      // [23] [24]
        pedido.calcularImporteAAbonar(hayStock);                                          // [25] [26]
        return pedido;
    }

    // =====================================================================================
    // CU-09 Consultar estado del pedido
    // =====================================================================================

    @Transactional(readOnly = true)
    public List<Pedido> listarPedidos(Long idUsuario) {
        return pedidoRepository.findByClienteIdUsuario(idUsuario);
    }

    @Transactional(readOnly = true)
    public EstadoPedidoDTO consultarEstado(Long idPedido, Long idUsuario) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        // Validar que el pedido pertenezca al usuario (RN-01)
        if (!pedido.getCliente().getIdUsuario().equals(idUsuario)) {
            throw new RuntimeException("No autorizado para consultar este pedido");
        }

        // Obtenemos el estado como String usando .name() del Enum ya que previamente realizamos el codigo por separado y al unirlo debimos adaptar el codigo
        String estadoStr = pedido.getEstado() != null ? pedido.getEstado().name() : "";

        EstadoPedidoDTO dto = new EstadoPedidoDTO(
                pedido.getIdPedido(),
                pedido.getFechaHora(),
                estadoStr,
                pedido.getMontoTotal() != null ? pedido.getMontoTotal().doubleValue() : 0.0
        );

        // Lógica avanzada de los flujos alternativos (aportando la info completa de tu compañero)
        List<String> informacion = new ArrayList<>();
        
        if ("PENDIENTE_DE_PAGO".equals(estadoStr)) {
            boolean esSena = pedido.getImporteAAbonar() != null && pedido.getMontoTotal() != null 
                    && pedido.getImporteAAbonar().compareTo(pedido.getMontoTotal()) < 0;
            informacion.add("Importe a abonar: $" + pedido.getImporteAAbonar()
                    + (esSena ? " (seña del 30 %, falta stock de productos que se fabrican a pedido)."
                              : " (100 % del total)."));
            LocalDateTime vencimiento = pedido.getVencimientoReserva();
            if (vencimiento != null && vencimiento.isAfter(LocalDateTime.now())) {
                informacion.add("Plazo para pagar: hasta el " + vencimiento.format(FORMATO_FECHA)
                        + " (vence la reserva de stock).");
            } else if (vencimiento != null) {
                informacion.add("La reserva de stock venció el " + vencimiento.format(FORMATO_FECHA)
                        + ": si no se confirma el pago, el pedido se cancela (RN-04).");
            }
            informacion.add("Todavía podés cancelar el pedido.");
            
        } else if ("EN_PRODUCCION".equals(estadoStr) || "EN_COLA_DE_PRODUCCION".equals(estadoStr)) {
            Integer dias = null;
            if (pedido.getDetalles() != null) {
                for (DetallePedido detalle : pedido.getDetalles()) {
                    Integer diasProducto = detalle.getProducto().getTiempoEstimadoProduccion();
                    if (detalle.getCantidadReservada() < detalle.getCantidad() && diasProducto != null
                            && (dias == null || diasProducto > dias)) {
                        dias = diasProducto;
                    }
                }
            }
            if (dias != null) {
                informacion.add("Tiempo estimado de producción: " + dias + " días.");
            }
            BigDecimal saldo = pedido.getMontoTotal() != null && pedido.getImporteAAbonar() != null
                    ? pedido.getMontoTotal().subtract(pedido.getImporteAAbonar())
                    : BigDecimal.ZERO;
            informacion.add("El saldo restante de $" + saldo
                    + " (70 %) podrá abonarse cuando se complete la producción.");
            informacion.add("EN_COLA_DE_PRODUCCION".equals(estadoStr)
                    ? "Todavía podés cancelar el pedido: se te reembolsaría el 100 % de la seña (RN-04)."
                    : "Todavía podés cancelar el pedido, pero la seña no se reembolsa porque ya está en producción (RN-04).");
                    
        } else if ("CANCELADO".equals(estadoStr)) {
            informacion.add(pedido.getFechaCancelacion() != null
                    ? "El pedido fue cancelado el " + pedido.getFechaCancelacion().format(FORMATO_FECHA) + "."
                    : "El pedido fue cancelado.");
                    
        } else if (estadoStr.contains("DESPACHADO") || estadoStr.contains("ENTREGADO") || estadoStr.contains("LISTO")) {
            informacion.add("El detalle del envío puede consultarse en «Consultar estado del envío».");
            
        } else if ("CONFIRMADO".equals(estadoStr)) {
            informacion.add("El pago está completo. El pedido ya no se puede cancelar.");
            
        } else {
            informacion.add("Estado actual registrado correctamente en el sistema.");
        }

        dto.setInformacionComplementaria(String.join(" ", informacion));
        return dto;
    }
}
