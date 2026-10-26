-- =====================================================================================
-- EcoGestión Verde: esquema de la base de datos según el DER (MySQL 8).
-- Spring Boot lo ejecuta en cada arranque (spring.sql.init.mode=always) y después carga
-- data.sql. Borra y vuelve a crear las tablas, así que los datos de prueba se reinician.
-- Los valores de los ENUM se tomaron de la especificación: el DER solo muestra ENUM(...).
-- =====================================================================================

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS solicitud_cancelacion;
DROP TABLE IF EXISTS pago;
DROP TABLE IF EXISTS detalle_pedido;
DROP TABLE IF EXISTS pedido;
DROP TABLE IF EXISTS item_carrito;
DROP TABLE IF EXISTS carrito;
DROP TABLE IF EXISTS producto;
DROP TABLE IF EXISTS empleado;
DROP TABLE IF EXISTS cliente;
DROP TABLE IF EXISTS usuario;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE usuario (
    id_usuario    INT          NOT NULL AUTO_INCREMENT,
    nombre        VARCHAR(100) NOT NULL,
    email         VARCHAR(120) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    tipo_usuario  ENUM('CLIENTE', 'EMPLEADO') NOT NULL,
    CONSTRAINT pk_usuario PRIMARY KEY (id_usuario),
    CONSTRAINT uq_usuario_email UNIQUE (email)
) ENGINE = InnoDB;

CREATE TABLE cliente (
    id_cliente INT          NOT NULL,
    telefono   VARCHAR(30)  NOT NULL,
    direccion  VARCHAR(200) NOT NULL,
    CONSTRAINT pk_cliente PRIMARY KEY (id_cliente),
    CONSTRAINT fk_cliente_usuario FOREIGN KEY (id_cliente) REFERENCES usuario (id_usuario)
) ENGINE = InnoDB;

CREATE TABLE empleado (
    id_empleado INT NOT NULL,
    rol         ENUM('VENTAS', 'TRANSPORTISTA', 'DUENO') NOT NULL,
    CONSTRAINT pk_empleado PRIMARY KEY (id_empleado),
    CONSTRAINT fk_empleado_usuario FOREIGN KEY (id_empleado) REFERENCES usuario (id_usuario)
) ENGINE = InnoDB;

CREATE TABLE producto (
    id_producto                INT           NOT NULL AUTO_INCREMENT,
    codigo                     VARCHAR(30)   NOT NULL,
    nombre                     VARCHAR(100)  NOT NULL,
    descripcion                VARCHAR(500)  NOT NULL,
    precio                     DECIMAL(12,2) NOT NULL,
    estado                     ENUM('DISPONIBLE', 'AGOTADO') NOT NULL,
    categoria                  ENUM('STOCK_HABITUAL', 'BAJO_DEMANDA') NOT NULL,
    tiempo_estimado_produccion TINYINT       NULL,
    stock_disponible           INT           NOT NULL,
    publicado                  TINYINT(1)    NOT NULL,
    CONSTRAINT pk_producto PRIMARY KEY (id_producto),
    CONSTRAINT uq_producto_codigo UNIQUE (codigo),
    CONSTRAINT ck_producto_precio CHECK (precio >= 0),
    CONSTRAINT ck_producto_stock CHECK (stock_disponible >= 0)
) ENGINE = InnoDB;

CREATE TABLE carrito (
    id_carrito INT NOT NULL AUTO_INCREMENT,
    id_cliente INT NOT NULL,
    CONSTRAINT pk_carrito PRIMARY KEY (id_carrito),
    CONSTRAINT uq_carrito_cliente UNIQUE (id_cliente),
    CONSTRAINT fk_carrito_cliente FOREIGN KEY (id_cliente) REFERENCES cliente (id_cliente)
) ENGINE = InnoDB;

CREATE TABLE item_carrito (
    id_carrito  INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad    INT NOT NULL,
    CONSTRAINT pk_item_carrito PRIMARY KEY (id_carrito, id_producto),
    CONSTRAINT fk_item_carrito_carrito FOREIGN KEY (id_carrito) REFERENCES carrito (id_carrito),
    CONSTRAINT fk_item_carrito_producto FOREIGN KEY (id_producto) REFERENCES producto (id_producto),
    CONSTRAINT ck_item_carrito_cantidad CHECK (cantidad > 0)
) ENGINE = InnoDB;

CREATE TABLE pedido (
    id_pedido           INT           NOT NULL AUTO_INCREMENT,
    id_cliente          INT           NOT NULL,
    fecha_hora          DATETIME      NOT NULL,
    estado              ENUM('PENDIENTE_DE_PAGO', 'EN_COLA_DE_PRODUCCION', 'EN_PRODUCCION', 'CONFIRMADO',
                             'LISTO_PARA_DESPACHO', 'DESPACHADO', 'EN_TRANSITO', 'ENTREGADO', 'CANCELADO') NOT NULL,
    monto_total         DECIMAL(12,2) NOT NULL,
    cantidad_productos  INT           NOT NULL,
    importe_a_abonar    DECIMAL(12,2) NOT NULL,
    vencimiento_reserva DATETIME      NULL,
    fecha_cancelacion   DATETIME      NULL,
    CONSTRAINT pk_pedido PRIMARY KEY (id_pedido),
    CONSTRAINT fk_pedido_cliente FOREIGN KEY (id_cliente) REFERENCES cliente (id_cliente)
) ENGINE = InnoDB;

CREATE TABLE detalle_pedido (
    id_pedido          INT           NOT NULL,
    id_producto        INT           NOT NULL,
    cantidad           INT           NOT NULL,
    precio_unitario    DECIMAL(12,2) NOT NULL,
    subtotal           DECIMAL(12,2) NOT NULL,
    cantidad_reservada INT           NOT NULL,
    CONSTRAINT pk_detalle_pedido PRIMARY KEY (id_pedido, id_producto),
    CONSTRAINT fk_detalle_pedido_pedido FOREIGN KEY (id_pedido) REFERENCES pedido (id_pedido),
    CONSTRAINT fk_detalle_pedido_producto FOREIGN KEY (id_producto) REFERENCES producto (id_producto),
    CONSTRAINT ck_detalle_pedido_cantidad CHECK (cantidad > 0),
    CONSTRAINT ck_detalle_pedido_reservada CHECK (cantidad_reservada BETWEEN 0 AND cantidad)
) ENGINE = InnoDB;

CREATE TABLE pago (
    id_pago         INT           NOT NULL AUTO_INCREMENT,
    id_pedido       INT           NOT NULL,
    tipo            ENUM('SENA', 'SALDO', 'TOTAL') NOT NULL,
    monto           DECIMAL(12,2) NOT NULL,
    medio_pago      ENUM('TARJETA_CREDITO', 'BILLETERA_VIRTUAL', 'TRANSFERENCIA', 'EFECTIVO') NOT NULL,
    estado          ENUM('PENDIENTE_CONFIRMACION', 'CONFIRMADO', 'RECHAZADO') NOT NULL,
    fecha_hora      DATETIME      NOT NULL,
    nro_transaccion VARCHAR(50)   NULL,
    CONSTRAINT pk_pago PRIMARY KEY (id_pago),
    CONSTRAINT fk_pago_pedido FOREIGN KEY (id_pedido) REFERENCES pedido (id_pedido)
) ENGINE = InnoDB;

CREATE TABLE solicitud_cancelacion (
    id_solicitud      INT           NOT NULL AUTO_INCREMENT,
    id_pedido         INT           NOT NULL,
    fecha_solicitud   DATETIME      NOT NULL,
    motivo            VARCHAR(300)  NULL,
    estado            ENUM('PENDIENTE_DE_REVISION', 'ACEPTADA', 'RECHAZADA') NOT NULL,
    id_resolutor      INT           NULL,
    monto_reembolsado DECIMAL(12,2) NULL,
    CONSTRAINT pk_solicitud_cancelacion PRIMARY KEY (id_solicitud),
    CONSTRAINT fk_solicitud_pedido FOREIGN KEY (id_pedido) REFERENCES pedido (id_pedido),
    CONSTRAINT fk_solicitud_resolutor FOREIGN KEY (id_resolutor) REFERENCES empleado (id_empleado)
) ENGINE = InnoDB;
