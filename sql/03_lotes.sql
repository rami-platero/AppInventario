-- AppInventario: lotes de los productos perecederos.
-- Ejecutar DESPUES de 02_datos.sql.
--
-- El vencimiento pertenece al lote y no al producto: un mismo articulo puede
-- tener varias tandas compradas en fechas distintas, cada una con su fecha.
-- Para los productos perecederos, producto.stock pasa a ser siempre la suma de
-- las cantidades de sus lotes, y eso es lo que verifica la ultima consulta.
--
-- Las fechas son relativas a CURRENT_DATE, asi que el escenario sigue siendo
-- util sin importar cuando se corra el script: siempre hay un lote vencido,
-- varios criticos y varios por vencer.

UPDATE producto
SET perecedero = TRUE
WHERE codigo IN ('LAC-001', 'LAC-002', 'LAC-003', 'PAN-001');

INSERT INTO lote (producto_idproducto, codigo, vencimiento, cantidad)
SELECT p.idproducto, d.codigo_lote, CURRENT_DATE + d.dias, d.cantidad
FROM (VALUES
    ('LAC-001', 'L-2411-A',  -2,  5),
    ('LAC-001', 'L-2411-B',   3,  4),
    ('LAC-001', 'L-2411-C',  20,  6),
    ('LAC-002', 'Y-0087',     5,  6),
    ('LAC-002', 'Y-0091',    25,  8),
    ('LAC-003', 'Q-553',     12,  4),
    ('PAN-001', NULL,         1,  4)
) AS d(producto, codigo_lote, dias, cantidad)
JOIN producto p ON p.codigo = d.producto;

-- Control: para cada perecedero, el stock del producto tiene que coincidir con
-- la suma de sus lotes. La columna "coincide" debe decir OK en todas las filas.
SELECT p.codigo,
       p.stock,
       COALESCE(SUM(l.cantidad), 0) AS suma_lotes,
       CASE WHEN p.stock = COALESCE(SUM(l.cantidad), 0) THEN 'OK' ELSE 'REVISAR' END AS coincide
FROM producto p
LEFT JOIN lote l ON l.producto_idproducto = p.idproducto
WHERE p.perecedero
GROUP BY p.codigo, p.stock
ORDER BY p.codigo;
