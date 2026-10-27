-- =====================================================================================
-- EcoGestión Verde: datos de prueba de Comprar (CU-05) y Consultar estado del pedido (CU-09).
-- Se cargan en cada arranque, después de schema.sql. Sirven tanto para MySQL como para H2.
-- La contraseña de todos los usuarios es: verde123  (guardada con BCrypt)
-- =====================================================================================

INSERT INTO usuario (id_usuario, nombre, email, password_hash, tipo_usuario) VALUES
    (1, 'Ana Gómez',      'ana@mail.test',          '$2a$10$c5pf2mzW/S5KYbBsLLRrkOZkiO7NOL51cy7paJQywStHqfDLH13.m', 'CLIENTE'),
    (2, 'Bruno Díaz',     'bruno@mail.test',        '$2a$10$ZO5I/IcAUpewgUZ4znSVi.0a42JyKwxLhhcYX1XZGiNhLFu.n.ETy', 'CLIENTE'),
    (3, 'Carla Ruiz',     'carla@mail.test',        '$2a$10$diu7RAwTjm62oSi1NM7qwuDE7c3Tm2tGZ86v5aVSQh6/BUoa5eABG', 'CLIENTE'),
    (4, 'Diego Paz',      'diego@mail.test',        '$2a$10$JNlrlQadXLutYPV4/ki52OGOl17kf.zVQwjaYtABqjHJf/Cybpx46', 'CLIENTE'),
    (5, 'Elena Sosa',     'elena@mail.test',        '$2a$10$53gmaGUwmUwx2mxV.OlRlew83u/8u5e6czrPOid2.FCNgwggjQV.m', 'CLIENTE'),
    (6, 'Federico Luna',  'fede@mail.test',         '$2a$10$Tqw11IHbAeUbDEsyEHqUEOPRL7nDmFccSRvnNHTIoAR3S5BiEYX4G', 'CLIENTE'),
    (7, 'Valeria Torres', 'ventas@verdegreen.test', '$2a$10$foKnuAl2GMyUGCQkl1SROuVkXSeBxxergGhscu6N/AdGZbYmSt0gm', 'EMPLEADO');

INSERT INTO cliente (id_cliente, telefono, direccion) VALUES
    (1, '2954-410001', 'Av. Siempre Verde 101'),
    (2, '2954-410002', 'Calle de los Pinos 202'),
    (3, '2954-410003', 'Pasaje Reciclado 303'),
    (4, '2954-410004', 'Bulevar Sustentable 404'),
    (5, '2954-410005', 'Calle del Bosque 505'),
    (6, '2954-410006', 'Av. del Parque 606');

INSERT INTO empleado (id_empleado, rol) VALUES
    (7, 'VENTAS');


-- Catálogo. Los productos 7 y 8 están dados de baja (publicado = 0) para probar el FE-02.
INSERT INTO producto (id_producto, codigo, nombre, descripcion, precio, estado, categoria,
                      tiempo_estimado_produccion, stock_disponible, publicado) VALUES
    (1, 'MAC-001', 'Maceta mediana de PET reciclado', 'Maceta de 20 cm fabricada con PET reciclado.',
        4500.00, 'DISPONIBLE', 'STOCK_HABITUAL', NULL, 50, 1),
    (2, 'BAN-001', 'Bandeja de PET reciclado', 'Bandeja rectangular apta para alimentos secos.',
        2800.00, 'DISPONIBLE', 'STOCK_HABITUAL', NULL, 120, 1),
    (3, 'BOT-001', 'Botella reutilizable de 1 L', 'Botella con tapa a rosca, libre de BPA.',
        3200.00, 'DISPONIBLE', 'STOCK_HABITUAL', NULL, 80, 1),
    (4, 'PLA-001', 'Set de 6 platos reciclados', 'Juego de seis platos playos de PET reciclado.',
        5600.00, 'DISPONIBLE', 'STOCK_HABITUAL', NULL, 40, 1),
    (5, 'BNC-001', 'Banco de plaza de PET', 'Banco para exterior de 1,5 m que se fabrica a pedido.',
        185000.00, 'DISPONIBLE', 'BAJO_DEMANDA', 10, 2, 1),
    (6, 'MES-001', 'Mesa de exterior de PET', 'Mesa para seis personas que se fabrica a pedido.',
        320000.00, 'AGOTADO', 'BAJO_DEMANDA', 15, 0, 1),
    (7, 'JUE-001', 'Juego infantil con tobogán', 'Juego para plazas con tobogán que se fabrica a pedido.',
        540000.00, 'AGOTADO', 'BAJO_DEMANDA', 15, 0, 0),
    (8, 'MAC-099', 'Maceta colgante (discontinuada)', 'Modelo dado de baja del catálogo.',
        3900.00, 'DISPONIBLE', 'STOCK_HABITUAL', NULL, 25, 0);

-- Un carrito por escenario. El cliente 6 (fede) no tiene carrito.
INSERT INTO carrito (id_carrito, id_cliente) VALUES
    (1, 1),
    (2, 2),
    (3, 3),
    (4, 4),
    (5, 5);

-- ana (carrito 1): camino feliz, hay stock de todo. Total 20600.00, se abona el 100 %.
-- bruno (carrito 2): FA-02, falta stock del banco y de la mesa. Total 879500.00, seña 263850.00.
-- carla (carrito 3): FE-02, la maceta colgante ya no está publicada. Queda un total de 11200.00.
-- diego (carrito 4): FE-02, ningún producto sigue publicado.
-- elena (carrito 5): carrito sin productos.
INSERT INTO item_carrito (id_carrito, id_producto, cantidad) VALUES
    (1, 1, 2),
    (1, 2, 3),
    (1, 3, 1),
    (2, 1, 1),
    (2, 5, 3),
    (2, 6, 1),
    (3, 4, 2),
    (3, 8, 1),
    (4, 7, 1),
    (4, 8, 2);

-- =====================================================================================
-- Consultar estado del pedido (CU-09). Federico (cliente 6, sin carrito) tiene un pedido en cada
-- situación de los flujos alternativos. Bruno (cliente 2) tiene uno para probar la RN-01.
-- Elena (cliente 5) no tiene pedidos (FA-01). Los pedidos que se generan con Comprar se agregan
-- con id 7 en adelante.
--   1  fede   PENDIENTE_DE_PAGO       FA-02: 100 %, la reserva vence en unas 20 h
--   2  fede   EN_COLA_DE_PRODUCCION   FA-03: seña pagada, se reembolsa si cancela
--   3  fede   EN_PRODUCCION           FA-03: la seña ya no se reembolsa si cancela
--   4  fede   CANCELADO               FA-05: informa la fecha de cancelación
--   5  fede   ENTREGADO               FA-06: el envío se consulta en otro caso de uso
--   6  bruno  CONFIRMADO              pago completo; fede no lo puede consultar (RN-01)
-- =====================================================================================
INSERT INTO pedido (id_pedido, id_cliente, fecha_hora, estado, monto_total, cantidad_productos,
                    importe_a_abonar, vencimiento_reserva, fecha_cancelacion) VALUES
    (1, 6, NOW() - INTERVAL 4 HOUR, 'PENDIENTE_DE_PAGO',          9000.00, 2,   9000.00,
        NOW() + INTERVAL 20 HOUR, NULL),
    (2, 6, '2026-10-20 10:00:00',  'EN_COLA_DE_PRODUCCION',     185000.00, 1,  55500.00,
        '2026-10-21 10:00:00', NULL),
    (3, 6, '2026-10-15 11:00:00',  'EN_PRODUCCION',             320000.00, 1,  96000.00,
        '2026-10-16 11:00:00', NULL),
    (4, 6, '2026-10-10 10:00:00',  'CANCELADO',                 185000.00, 1,  55500.00,
        '2026-10-11 10:00:00', '2026-10-13 16:00:00'),
    (5, 6, '2026-10-01 09:30:00',  'ENTREGADO',                   3200.00, 1,   3200.00,
        '2026-10-02 09:30:00', NULL),
    (6, 2, '2026-10-22 18:00:00',  'CONFIRMADO',                  5600.00, 1,   5600.00,
        '2026-10-23 18:00:00', NULL);

INSERT INTO detalle_pedido (id_pedido, id_producto, cantidad, precio_unitario, subtotal, cantidad_reservada) VALUES
    (1, 1, 2,   4500.00,   9000.00, 2),
    (2, 5, 1, 185000.00, 185000.00, 0),
    (3, 6, 1, 320000.00, 320000.00, 0),
    (4, 5, 1, 185000.00, 185000.00, 0),
    (5, 3, 1,   3200.00,   3200.00, 1),
    (6, 4, 1,   5600.00,   5600.00, 1);
