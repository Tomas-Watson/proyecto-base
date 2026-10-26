package com.aydsii.demo.service;

import com.aydsii.demo.domain.Carrito;
import com.aydsii.demo.domain.DetallePedido;
import com.aydsii.demo.domain.ItemCarrito;
import com.aydsii.demo.domain.Pedido;
import com.aydsii.demo.domain.Producto;
import com.aydsii.demo.dto.ResumenCompraDTO;
import com.aydsii.demo.repository.CarritoRepository;
import com.aydsii.demo.repository.PedidoRepository;
import com.aydsii.demo.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * «Service» PedidoService del diagrama de clases de diseño del caso de uso Comprar
 * (CU-01 en los diagramas y la especificación, CU-05 en el README de casos de uso).
 * <p>
 * Los números entre corchetes en los comentarios son los mensajes del diagrama de secuencia.
 */
@Service
public class PedidoService {

    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);

    private final CarritoRepository carritoRepository;
    private final ProductoRepository productoRepository;
    private final PedidoRepository pedidoRepository;

    public PedidoService(CarritoRepository carritoRepository,
                         ProductoRepository productoRepository,
                         PedidoRepository pedidoRepository) {
        this.carritoRepository = carritoRepository;
        this.productoRepository = productoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    /**
     * Pasos 1 a 3 del flujo principal: arma el detalle de la compra, calcula el total, verifica el
     * stock e informa el importe a abonar.
     * <p>
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
     * Pasos 4 a 7 del flujo principal: genera el pedido en «Pendiente de pago», reserva el stock
     * por 24 horas y vacía el carrito.
     * <p>
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
}
