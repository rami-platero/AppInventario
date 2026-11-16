-- AppInventario: vuelve los datos al estado inicial.
--
-- OJO: borra TODAS las filas, incluidos los usuarios y los negocios creados
-- probando la aplicacion (alta, login, etc.). La estructura queda intacta.
--
-- Despues de correr este script hay que ejecutar, en este orden:
--   sql/02_datos.sql
--   sql/03_lotes.sql
--   sql/04_negocios.sql
--
-- RESTART IDENTITY reinicia los contadores de las claves primarias, asi que los
-- ids vuelven a empezar en 1 y la demo queda igual que la primera vez.

TRUNCATE TABLE movimiento_stock, lote, producto_proveedor, producto, proveedor, categoria,
    membresia, negocio, usuario
RESTART IDENTITY;

-- categoria/proveedor/producto/movimiento_stock quedan con negocio_idnegocio NOT NULL
-- desde la primera vez que corrio 04_negocios.sql. Se libera momentaneamente para que
-- 02_datos.sql y 03_lotes.sql puedan insertar sin esa columna, igual que en una base
-- nueva; 04_negocios.sql la vuelve a poner NOT NULL al final, como siempre.
ALTER TABLE categoria ALTER COLUMN negocio_idnegocio DROP NOT NULL;
ALTER TABLE proveedor ALTER COLUMN negocio_idnegocio DROP NOT NULL;
ALTER TABLE producto ALTER COLUMN negocio_idnegocio DROP NOT NULL;
ALTER TABLE movimiento_stock ALTER COLUMN negocio_idnegocio DROP NOT NULL;
