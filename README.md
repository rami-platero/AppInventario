# Depósito — inventario de stock

Aplicación web para gestionar el stock de un negocio, con **aviso automático de los productos
que hay que reponer** y **control de vencimientos por lote**. Trabajo Práctico Integrador de
Desarrollo Backend III, construido sobre Jakarta EE 10.

La aplicación es agnóstica del rubro: los productos, las categorías y las unidades los define
quien la usa. Los datos de ejemplo son un almacén de barrio, pero sirve igual para una
ferretería, una farmacia o un depósito de insumos.

## Qué hace

### Multi-negocio y roles

Cualquiera puede registrarse y, al hacerlo, crea su propio negocio. Un mismo usuario puede
pertenecer a varios negocios con un rol distinto en cada uno, y cada negocio ve únicamente sus
propios datos: categorías, proveedores, productos y movimientos.

| Rol | Puede hacer |
| --- | --- |
| **Propietario** | Todo: catálogo, movimientos y administrar quién pertenece al negocio |
| **Empleado** | Registrar movimientos de stock y consultar todo. No gestiona catálogo ni gente |

Desde **Empleados** el propietario agrega a alguien por su email (tiene que estar ya
registrado), le asigna un rol, y puede cambiarlo o quitarlo. Siempre tiene que quedar al menos
un propietario en el negocio.

### Aviso de reposición

Cada producto tiene un **stock mínimo**. Según cómo esté el stock respecto de ese mínimo, la
aplicación lo clasifica y lo muestra en todas las pantallas:

| Estado | Condición |
| --- | --- |
| Agotado | `stock = 0` |
| Stock bajo | `stock <= stock_minimo` |
| Normal | `stock > stock_minimo` |

La pantalla **Hay que reponer** ordena los productos por urgencia, calcula cuántas unidades
faltan para llegar al objetivo de reposición (el doble del mínimo), sugiere el proveedor más
barato de los que venden ese producto y estima el costo de la compra. El botón *Reponer* abre
el formulario de movimiento ya cargado con esa cantidad y ese proveedor.

### Control de vencimientos

El vencimiento pertenece al **lote**, no al producto: un mismo artículo puede tener varias
tandas compradas en fechas distintas. Los productos marcados como perecederos llevan lotes, y
su stock es siempre la suma de ellos.

- Las **entradas** piden la fecha de vencimiento y crean o engordan un lote.
- Las **salidas** descuentan del lote que vence primero (FEFO, *first expired, first out*).
- Los **ajustes** se hacen sobre un lote puntual.

La pantalla **Vencimientos** lista los lotes vencidos o próximos a vencer y sugiere qué hacer
con cada uno. La sugerencia sale de medir el ritmo de venta observado del producto —las
unidades que salieron divididas por los días de historial que el sistema realmente tiene— y
proyectar cuántas unidades sobrarían antes del vencimiento.

Si hay menos de 3 salidas o menos de 7 días de historial, **no proyecta nada**: muestra "sin
datos suficientes" y explica qué falta. El porcentaje de descuento es una heurística sobre
cuánto hay que acelerar la venta, y se muestra siempre junto a los datos que lo originan: la
aplicación no tiene datos de elasticidad precio-demanda y no finge tenerlos.

### Integridad del stock

El stock nunca se escribe a mano. Todo cambio pasa por un **movimiento** (entrada, salida o
ajuste) que se registra en una única transacción: se bloquea la fila del producto, se valida
que el stock no quede negativo, se actualiza y se guarda el movimiento con el stock resultante.
Si algo falla, no se guarda nada. Por eso, al editar un producto, el campo de stock aparece
bloqueado.

El historial no se edita ni se borra: un error se corrige con un movimiento de ajuste.

### Pantallas

| Pantalla | Qué muestra |
| --- | --- |
| **Panel** | Totales del inventario, productos a reponer y últimos movimientos |
| **Hay que reponer** | Productos bajo el mínimo, con faltante, proveedor sugerido y costo |
| **Vencimientos** | Lotes vencidos o por vencer, con la sugerencia de qué hacer |
| **Productos** | Catálogo con buscador, filtros por categoría y estado, y ordenamiento |
| **Categorías** | Alta y baja, con productos y alertas por categoría |
| **Proveedores** | Alta y baja, con la cantidad de productos que provee cada uno |
| **Movimientos** | Historial filtrable por producto y por tipo |
| **Consultas** | Más vendidos, compras por proveedor, proveedores con más de dos productos y productos sin proveedor |
| **Empleados** | Quién pertenece al negocio y con qué rol (sólo propietario) |

## Stack

| Pieza | Versión | Para qué |
| --- | --- | --- |
| Jakarta EE | 10 | Plataforma: servlets, EJB, JPA, Bean Validation |
| GlassFish | 7.0.15 | Servidor de aplicaciones |
| PostgreSQL | 14+ | Base de datos |
| EclipseLink | el que trae GlassFish | Proveedor de persistencia (JPA) |
| JSP + JSTL | Jakarta Tags | Vistas |
| Maven | — | Compilación y empaquetado (WAR) |
| JDK | Temurin 21 | Entorno de ejecución del servidor |

La hoja de estilos está escrita a mano, sin frameworks, con modo claro y oscuro según la
preferencia del sistema operativo.

## Puesta en marcha

### 1. Base de datos

Crear una base llamada `inventariodb` en PostgreSQL y ejecutar, en este orden, desde el Query
Tool de pgAdmin4:

1. [`sql/01_esquema.sql`](sql/01_esquema.sql) — crea las tablas del inventario con sus claves,
   restricciones `CHECK` e índices.
2. [`sql/02_datos.sql`](sql/02_datos.sql) — carga el almacén de ejemplo: 5 categorías, 4
   proveedores, 13 productos y su historial de movimientos.
3. [`sql/03_lotes.sql`](sql/03_lotes.sql) — marca cuatro productos como perecederos y les crea
   sus lotes.
4. [`sql/04_negocios.sql`](sql/04_negocios.sql) — agrega el multi-negocio: crea `usuario`,
   `negocio` y `membresia`, y les asigna a los datos ya cargados un negocio por defecto
   ("Almacén de Barrio") con dos cuentas de prueba:

   | Email | Contraseña | Rol |
   | --- | --- | --- |
   | `dueno@deposito.com` | `deposito123` | Propietario |
   | `empleado@deposito.com` | `empleado123` | Empleado |

Ningún `INSERT` usa ids fijos: siempre se buscan por nombre o por código. El stock de cada
producto y el stock resultante de cada movimiento no se escriben a mano, se calculan con una
suma corrida sobre el historial, así los datos de prueba no pueden quedar inconsistentes. Las
fechas de vencimiento son relativas a `CURRENT_DATE`, de modo que siempre hay un lote vencido,
varios críticos y varios por vencer.

Para volver a la demo después de probar, ejecutar
[`sql/00_reiniciar_datos.sql`](sql/00_reiniciar_datos.sql) y repetir los pasos 2 a 4. El
script 04 es idempotente: se puede volver a correr las veces que hagan falta.

### 2. Driver de PostgreSQL en GlassFish

Copiar el `.jar` del driver en `glassfish/domains/domain1/lib` y reiniciar el servidor.

### 3. Recurso JDBC

Crear un pool de conexiones y un recurso JDBC con nombre JNDI `jdbc/bdinventario`:

- **Resource Type:** `java.sql.Driver`
- **Driver Classname:** `org.postgresql.Driver`
- **Datasource Classname:** vacío
- Propiedades: `url` = `jdbc:postgresql://localhost:5432/inventariodb`, más `user` y
  `password`.

El pool tiene que ser de tipo `java.sql.Driver`. Con un pool de tipo
`ConnectionPoolDataSource` el *Ping* funciona igual, pero el asistente de entidades de NetBeans
falla con "The name of the driver class for the datasource is missing".

### 4. Compilar y desplegar

Desde NetBeans: clic derecho en el proyecto, **Clean and Build**, y después **Run**.

Desde la línea de comandos:

```bash
mvn clean package
asadmin deploy --force=true target/AppInventario-1.0-SNAPSHOT.war
```

La aplicación queda en `http://localhost:8080/AppInventario-1.0-SNAPSHOT/`.

## Modelo de datos

Nueve tablas: las seis del inventario y tres para el multi-negocio.

```
usuario                        negocio                       membresia
-------                        -------                       ---------
idusuario     INT PK IDENTITY  idnegocio      INT PK IDENTITY usuario_idusuario  INT NN ─┐ PK
email         VARCHAR(80) NN   nombre         VARCHAR(60) NN  negocio_idnegocio  INT NN ─┘ compuesta
       UNIQUE                 fecha_creacion TIMESTAMP NN    rol                VARCHAR(12) NN
password_hash VARCHAR(64) NN
nombre        VARCHAR(60) NN

categoria                      proveedor
---------                      ---------
idcategoria   INT PK IDENTITY  idproveedor  INT PK IDENTITY
nombre        VARCHAR(45) NN   nombre       VARCHAR(45) NN
negocio_idnegocio  INT NN  ┐   telefono     VARCHAR(20)
     UNIQUE (con nombre)   │   email        VARCHAR(60)
                           │   negocio_idnegocio  INT NN  (UNIQUE con nombre)
                           ▼
                        negocio

producto                                  producto_proveedor
--------                                  ------------------
idproducto             INT PK IDENTITY    producto_idproducto    INT NN  ─┐ PK
codigo                 VARCHAR(20) NN     proveedor_idproveedor  INT NN  ─┘ compuesta
   UNIQUE (con negocio)                  precio_compra          NUMERIC(12,2) NN
nombre                 VARCHAR(60) NN     dias_entrega           INT
descripcion            VARCHAR(200)       codigo_proveedor       VARCHAR(30)
unidad                 VARCHAR(15) NN
stock                  INT NN >= 0        movimiento_stock
stock_minimo           INT NN >= 0        ----------------
precio                 NUMERIC(12,2) NN   idmovimiento           INT PK IDENTITY
perecedero             BOOLEAN NN         producto_idproducto    INT NN -> producto
categoria_idcategoria  INT NN             proveedor_idproveedor  INT    -> proveedor
negocio_idnegocio      INT NN             lote_idlote            INT    -> lote
                                          tipo                   VARCHAR(10) NN
lote                                      cantidad               INT NN >= 0
----                                      stock_resultante       INT NN >= 0
idlote                 INT PK IDENTITY    motivo                 VARCHAR(200)
producto_idproducto    INT NN             fecha                  TIMESTAMP NN
codigo                 VARCHAR(30)        negocio_idnegocio      INT NN
vencimiento            DATE NN
cantidad               INT NN >= 0
```

`tipo` admite `ENTRADA`, `SALIDA` o `AJUSTE`, controlado por un `CHECK`. `membresia.rol` admite
`PROPIETARIO` o `EMPLEADO`, también con un `CHECK`.

### Decisiones de modelado

- **`producto.stock` está desnormalizado a propósito.** Se podría calcular como la suma de los
  movimientos, pero se guarda en la fila para que los listados no tengan que agregar el
  historial completo en cada pantalla. `movimiento_stock.stock_resultante` es la contrapartida:
  permite verificar que el stock guardado es el correcto.
- **`producto_proveedor` es una relación muchos a muchos con clave compuesta.** Un producto
  real se compra a varios proveedores, cada uno con su precio y su demora. Con un único
  `proveedor_idproveedor` dentro de `producto` se perdería el dato que más sirve: a quién
  conviene comprarle. El proveedor recomendado no se guarda, se calcula.
- **`movimiento_stock.proveedor_idproveedor` es nullable** porque sólo las entradas tienen
  proveedor.
- **Ningún `@OneToMany` lleva `cascade`**: borrar una categoría no tiene que arrastrar sus
  productos.
- **`membresia` es la relación muchos a muchos entre `usuario` y `negocio`, con clave
  compuesta** igual que `producto_proveedor`: un usuario puede pertenecer a varios negocios, y
  cada pertenencia tiene su propio rol.
- **La unicidad de `categoria.nombre` y `producto.codigo` es por negocio, no global**: dos
  negocios distintos pueden tener cada uno su categoría "Bebidas" sin chocar.
- **Las contraseñas se guardan con SHA-256**, no en texto plano. Es una simplificación
  académica: un sistema real agregaría una sal por usuario (`bcrypt` o `Argon2`) para que dos
  contraseñas iguales no produzcan el mismo hash.

## Estructura del proyecto

```
src/main/java/
├── entities/     las 10 clases del mapeo JPA, incluidas las dos de clave compuesta
├── sessions/     los EJB de negocio: un facade por entidad, la excepción de negocio,
│                 dos JavaBeans de transporte para los datos calculados y SesionActual
└── controller/   Manejador.java (el único servlet) y el filtro de sesión/negocio

src/main/webapp/
├── css/estilos.css              fuera de WEB-INF, para que el navegador pueda pedirlo
├── index.jsp                    redirige al panel
└── WEB-INF/
    ├── views/                   las 21 pantallas
    └── views/partes/            cabecera, pie y los fragmentos de badge y barra

src/main/resources/META-INF/persistence.xml
sql/                             esquema, datos de prueba, lotes y reinicio
```

Las vistas están dentro de `WEB-INF` a propósito: así no se pueden pedir por URL y saltearse el
servlet, que es quien carga los datos.

## Validación

Los datos se controlan en tres capas, y cada una atrapa cosas distintas:

- **En el navegador:** `required`, `maxlength` igual al largo de cada columna, `min="0"` y
  `type="email"`. Es cómodo, pero se puede saltear.
- **En el servidor:** obligatoriedad, largos, números válidos y **duplicados**, que el navegador
  no puede detectar. Si algo falla, el formulario vuelve con la lista de errores sin tocar la
  base.
- **En la base de datos:** claves únicas, `CHECK` y claves foráneas, como última red.

Antes de eliminar una fila con relaciones, el facade consulta `hasRelations` y, si devuelve
`true`, no borra: redirige con el motivo, que la aplicación muestra como aviso.

## Consultas JPQL

| Para qué | Consulta |
| --- | --- |
| Productos a reponer | `SELECT p FROM Producto p WHERE p.stock <= p.stockMinimo ORDER BY p.stock, p.nombre` |
| Valor del inventario | `SELECT SUM(p.stock * p.precio) FROM Producto p` |
| Más vendidos | `SELECT m.productoIdproducto, SUM(m.cantidad) FROM MovimientoStock m WHERE m.tipo = 'SALIDA' GROUP BY m.productoIdproducto ORDER BY SUM(m.cantidad) DESC` |
| Compras por proveedor | `SELECT m.proveedorIdproveedor, COUNT(m), SUM(m.cantidad) FROM MovimientoStock m WHERE m.tipo = 'ENTRADA' GROUP BY m.proveedorIdproveedor` |
| Proveedores con más de 2 productos | `SELECT p FROM Proveedor p WHERE SIZE(p.productoProveedorCollection) > 2` |
| Productos sin proveedor | `SELECT p FROM Producto p WHERE p.productoProveedorCollection IS EMPTY` |
| Mejor proveedor de un producto | `SELECT pp FROM ProductoProveedor pp WHERE pp.productoProveedorPK.productoIdproducto = :id ORDER BY pp.precioCompra` |
| Lotes por vencer | `SELECT l FROM Lote l WHERE l.cantidad > 0 AND l.vencimiento <= :limite ORDER BY l.vencimiento` |

Un detalle que costó encontrar: contar los productos de una categoría con
`SIZE(c.productoCollection)` dentro del `SELECT` hace que EclipseLink genere un *inner join*, y
eso descarta las categorías sin productos — una categoría recién creada no aparecía en el
listado. La versión correcta usa `LEFT JOIN` con `COUNT` y `GROUP BY` explícito.

## Licencia

Trabajo académico. Universidad — Desarrollo Backend III.
