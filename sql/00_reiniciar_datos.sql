-- AppInventario: vuelve los datos al estado inicial.
--
-- OJO: borra TODAS las filas de las seis tablas. La estructura queda intacta.
-- Sirve para volver a la demo despues de haber probado la aplicacion.
--
-- Despues de correr este script hay que ejecutar, en este orden:
--   sql/02_datos.sql
--   sql/03_lotes.sql
--
-- RESTART IDENTITY reinicia los contadores de las claves primarias, asi que los
-- ids vuelven a empezar en 1 y la demo queda igual que la primera vez.

TRUNCATE TABLE movimiento_stock, lote, producto_proveedor, producto, proveedor, categoria
RESTART IDENTITY;
