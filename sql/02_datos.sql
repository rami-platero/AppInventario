-- AppInventario: datos de prueba (un almacen de barrio).
-- Ejecutar DESPUES de 01_esquema.sql.
--
-- Ningun INSERT usa ids fijos: siempre se buscan por nombre o por codigo.
-- El stock de cada producto y el stock_resultante de cada movimiento NO se
-- escriben a mano: se calculan al final a partir del historial, para que los
-- datos de prueba no puedan quedar inconsistentes.

INSERT INTO categoria (nombre) VALUES
    ('Almacen'),
    ('Bebidas'),
    ('Lacteos'),
    ('Panificados'),
    ('Limpieza');

INSERT INTO proveedor (nombre, telefono, email) VALUES
    ('Distribuidora del Centro', '3514567890', 'ventas@distcentro.com.ar'),
    ('Mayorista Norte',          '3511234567', 'pedidos@maynorte.com.ar'),
    ('Lacteos San Miguel',       '3517654321', 'contacto@sanmiguel.com.ar'),
    ('Panaderia La Esquina',     '3512223344', 'laesquina@gmail.com');

INSERT INTO producto (codigo, nombre, descripcion, unidad, stock_minimo, precio, categoria_idcategoria)
SELECT d.codigo, d.nombre, d.descripcion, d.unidad, d.stock_minimo, d.precio, c.idcategoria
FROM (VALUES
    ('ALM-001', 'Arroz largo fino 1 kg',   'Paquete de 1 kg',       'unidad', 10, 1850.00, 'Almacen'),
    ('ALM-002', 'Fideos guiseros 500 g',   'Paquete de 500 g',      'unidad', 12, 1200.00, 'Almacen'),
    ('ALM-003', 'Aceite de girasol 1,5 L', 'Botella de 1,5 litros', 'unidad',  8, 3900.00, 'Almacen'),
    ('ALM-004', 'Yerba mate 1 kg',         'Paquete de 1 kg',       'unidad',  6, 5600.00, 'Almacen'),
    ('BEB-001', 'Gaseosa cola 2,25 L',     'Botella retornable',    'unidad', 12, 2700.00, 'Bebidas'),
    ('BEB-002', 'Agua mineral 2 L',        'Botella sin gas',       'unidad', 10, 1400.00, 'Bebidas'),
    ('BEB-003', 'Cerveza rubia 1 L',       'Botella de litro',      'unidad', 12, 2900.00, 'Bebidas'),
    ('LAC-001', 'Leche entera sachet 1 L', 'Sachet de 1 litro',     'unidad', 20, 1600.00, 'Lacteos'),
    ('LAC-002', 'Yogur bebible 900 g',     'Botella de 900 g',      'unidad',  8, 2300.00, 'Lacteos'),
    ('LAC-003', 'Queso cremoso',           'Se vende por kilo',     'kg',      5, 9800.00, 'Lacteos'),
    ('PAN-001', 'Pan de mesa 500 g',       'Bolsa de 500 g',        'unidad', 10, 1750.00, 'Panificados'),
    ('LIM-001', 'Lavandina 1 L',           'Botella de 1 litro',    'unidad',  6, 1100.00, 'Limpieza'),
    ('LIM-002', 'Detergente 750 ml',       'Botella de 750 ml',     'unidad',  6, 2100.00, 'Limpieza')
) AS d(codigo, nombre, descripcion, unidad, stock_minimo, precio, categoria)
JOIN categoria c ON c.nombre = d.categoria;

-- Que proveedores venden cada producto y a cuanto. Varios productos tienen dos
-- proveedores con precios distintos: de ahi sale la consulta "mejor proveedor".
INSERT INTO producto_proveedor (producto_idproducto, proveedor_idproveedor, precio_compra, dias_entrega, codigo_proveedor)
SELECT p.idproducto, v.idproveedor, d.precio_compra, d.dias_entrega, d.codigo_proveedor
FROM (VALUES
    ('ALM-001', 'Distribuidora del Centro', 1320.00, 3, 'DC-ARR-1K'),
    ('ALM-001', 'Mayorista Norte',          1280.00, 7, 'MN-10021'),
    ('ALM-002', 'Distribuidora del Centro',  860.00, 3, 'DC-FID-500'),
    ('ALM-002', 'Mayorista Norte',           910.00, 7, 'MN-10044'),
    ('ALM-003', 'Mayorista Norte',          2950.00, 7, 'MN-20310'),
    ('ALM-004', 'Distribuidora del Centro', 4100.00, 3, 'DC-YER-1K'),
    ('ALM-004', 'Mayorista Norte',          3980.00, 7, 'MN-30188'),
    ('BEB-001', 'Distribuidora del Centro', 1950.00, 2, 'DC-GAS-225'),
    ('BEB-002', 'Distribuidora del Centro', 1000.00, 2, 'DC-AGU-2L'),
    ('BEB-002', 'Mayorista Norte',           960.00, 7, 'MN-40012'),
    ('BEB-003', 'Mayorista Norte',          2150.00, 7, 'MN-40551'),
    ('LAC-001', 'Lacteos San Miguel',       1150.00, 1, 'SM-LE-1L'),
    ('LAC-002', 'Lacteos San Miguel',       1690.00, 1, 'SM-YB-900'),
    ('LAC-003', 'Lacteos San Miguel',       7400.00, 2, 'SM-QC-KG'),
    ('PAN-001', 'Panaderia La Esquina',     1180.00, 1, 'LE-PAN-500'),
    ('LIM-001', 'Mayorista Norte',           720.00, 7, 'MN-50077'),
    ('LIM-002', 'Mayorista Norte',          1450.00, 7, 'MN-50102'),
    ('LIM-002', 'Distribuidora del Centro', 1510.00, 3, 'DC-DET-750')
) AS d(codigo, proveedor, precio_compra, dias_entrega, codigo_proveedor)
JOIN producto p ON p.codigo = d.codigo
JOIN proveedor v ON v.nombre = d.proveedor;

-- Carga inicial: una ENTRADA por producto, hace 30 dias, comprada al proveedor
-- mas barato de cada uno. stock_resultante va en 0 y se recalcula al final.
INSERT INTO movimiento_stock (producto_idproducto, proveedor_idproveedor, tipo, cantidad, stock_resultante, motivo, fecha)
SELECT p.idproducto, mejor.proveedor_idproveedor, 'ENTRADA', d.cantidad, 0,
       'Carga inicial de inventario', CURRENT_TIMESTAMP - INTERVAL '30 days'
FROM (VALUES
    ('ALM-001', 40), ('ALM-002', 30), ('ALM-003', 20), ('ALM-004', 18),
    ('BEB-001', 36), ('BEB-002', 24), ('BEB-003', 48),
    ('LAC-001', 60), ('LAC-002', 24), ('LAC-003', 12),
    ('PAN-001', 30), ('LIM-001', 18), ('LIM-002', 16)
) AS d(codigo, cantidad)
JOIN producto p ON p.codigo = d.codigo
JOIN LATERAL (
    SELECT pp.proveedor_idproveedor
    FROM producto_proveedor pp
    WHERE pp.producto_idproducto = p.idproducto
    ORDER BY pp.precio_compra, pp.proveedor_idproveedor
    LIMIT 1
) AS mejor ON TRUE;

-- Ventas de los ultimos 25 dias. Cada producto tiene al menos 2 salidas
-- repartidas en el tiempo, para que el ritmo de venta se pueda estimar.
INSERT INTO movimiento_stock (producto_idproducto, tipo, cantidad, stock_resultante, motivo, fecha)
SELECT p.idproducto, 'SALIDA', d.cantidad, 0, 'Venta en mostrador',
       CURRENT_TIMESTAMP - make_interval(days => d.dias)
FROM (VALUES
    ('ALM-001',  5, 22), ('ALM-001',  4, 15), ('ALM-001',  3,  9), ('ALM-001',  4,  3),
    ('ALM-002',  8, 20), ('ALM-002',  6, 13), ('ALM-002',  6,  7), ('ALM-002',  4,  2),
    ('ALM-003',  6, 18), ('ALM-003',  7, 11), ('ALM-003',  7,  4),
    ('ALM-004',  2, 16), ('ALM-004',  2,  8), ('ALM-004',  2,  1),
    ('BEB-001',  6, 21), ('BEB-001',  5, 14), ('BEB-001',  6,  8), ('BEB-001',  5,  2),
    ('BEB-002',  5, 19), ('BEB-002',  6, 12), ('BEB-002',  5,  5),
    ('BEB-003',  8, 17), ('BEB-003',  6, 10), ('BEB-003',  6,  3),
    ('LAC-001', 12, 20), ('LAC-001', 10, 14), ('LAC-001', 11,  8), ('LAC-001', 12,  2),
    ('LAC-002',  4, 12), ('LAC-002',  3,  6), ('LAC-002',  3,  1),
    ('LAC-003',  3, 15), ('LAC-003',  2,  9), ('LAC-003',  3,  4),
    ('PAN-001',  9, 16), ('PAN-001',  8, 10), ('PAN-001',  9,  4),
    ('LIM-001',  2, 13), ('LIM-001',  2,  6),
    ('LIM-002',  3, 14), ('LIM-002',  3,  5)
) AS d(codigo, cantidad, dias)
JOIN producto p ON p.codigo = d.codigo;

-- El stock que deja cada movimiento, calculado como suma corrida del historial
-- de su producto. Es la misma cuenta que hace el facade al registrar uno nuevo.
WITH acumulado AS (
    SELECT m.idmovimiento,
           SUM(CASE m.tipo WHEN 'ENTRADA' THEN m.cantidad ELSE -m.cantidad END)
               OVER (PARTITION BY m.producto_idproducto
                     ORDER BY m.fecha, m.idmovimiento
                     ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS saldo
    FROM movimiento_stock m
)
UPDATE movimiento_stock m
SET stock_resultante = a.saldo
FROM acumulado a
WHERE a.idmovimiento = m.idmovimiento;

-- El stock del producto es el stock_resultante de su ultimo movimiento.
UPDATE producto p
SET stock = COALESCE((
    SELECT m.stock_resultante
    FROM movimiento_stock m
    WHERE m.producto_idproducto = p.idproducto
    ORDER BY m.fecha DESC, m.idmovimiento DESC
    LIMIT 1
), 0);
