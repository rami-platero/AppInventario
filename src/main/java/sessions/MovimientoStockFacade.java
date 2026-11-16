package sessions;

import entities.Lote;
import entities.MovimientoStock;
import entities.Producto;
import entities.Proveedor;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TemporalType;
import jakarta.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

@Stateless
public class MovimientoStockFacade extends AbstractFacade<MovimientoStock> {

    public static final String ENTRADA = "ENTRADA";
    public static final String SALIDA = "SALIDA";
    public static final String AJUSTE = "AJUSTE";

    @PersistenceContext(unitName = "AppInventarioPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MovimientoStockFacade() {
        super(MovimientoStock.class);
    }

    public MovimientoStock registrar(Integer idProducto, String tipo, int cantidad,
            Integer idProveedor, String motivo) throws MovimientoInvalidoException {
        return registrar(idProducto, tipo, cantidad, idProveedor, motivo, null, null, null);
    }

    public MovimientoStock registrar(Integer idProducto, String tipo, int cantidad,
            Integer idProveedor, String motivo, Date vencimiento, String codigoLote,
            Integer idLote) throws MovimientoInvalidoException {

        Producto producto = em.find(Producto.class, idProducto, LockModeType.PESSIMISTIC_WRITE);
        if (producto == null) {
            throw new MovimientoInvalidoException("El producto indicado no existe.");
        }
        if (!ENTRADA.equals(tipo) && !SALIDA.equals(tipo) && !AJUSTE.equals(tipo)) {
            throw new MovimientoInvalidoException("Tipo de movimiento desconocido: " + tipo);
        }
        if (cantidad < 0) {
            throw new MovimientoInvalidoException("La cantidad no puede ser negativa.");
        }
        if (cantidad == 0 && !AJUSTE.equals(tipo)) {
            throw new MovimientoInvalidoException("La cantidad tiene que ser mayor que cero.");
        }

        Lote lote;
        int resultante;
        if (producto.getPerecedero()) {
            lote = aplicarEnLotes(producto, tipo, cantidad, vencimiento, codigoLote, idLote);
            em.flush();
            resultante = sumaDeLotes(idProducto);
        } else {
            lote = null;
            resultante = calcularResultante(producto, tipo, cantidad);
            if (resultante < 0) {
                throw new MovimientoInvalidoException("Stock insuficiente: hay "
                        + producto.getStock() + " " + producto.getUnidad() + " de "
                        + producto.getNombre() + " y se intentan retirar " + cantidad + ".");
            }
        }

        producto.setStock(resultante);

        MovimientoStock movimiento = new MovimientoStock();
        movimiento.setProductoIdproducto(producto);
        movimiento.setNegocioIdnegocio(producto.getNegocioIdnegocio());
        movimiento.setTipo(tipo);
        movimiento.setCantidad(cantidad);
        movimiento.setStockResultante(resultante);
        movimiento.setFecha(new Date());
        movimiento.setLoteIdlote(lote);
        if (motivo != null && !motivo.trim().isEmpty()) {
            movimiento.setMotivo(motivo.trim());
        }
        if (idProveedor != null && ENTRADA.equals(tipo)) {
            movimiento.setProveedorIdproveedor(em.find(Proveedor.class, idProveedor));
        }
        em.persist(movimiento);
        return movimiento;
    }

    private int calcularResultante(Producto producto, String tipo, int cantidad) {
        if (ENTRADA.equals(tipo)) {
            return producto.getStock() + cantidad;
        }
        if (SALIDA.equals(tipo)) {
            return producto.getStock() - cantidad;
        }
        return cantidad;
    }

    private Lote aplicarEnLotes(Producto producto, String tipo, int cantidad, Date vencimiento,
            String codigoLote, Integer idLote) throws MovimientoInvalidoException {

        if (ENTRADA.equals(tipo)) {
            if (vencimiento == null) {
                throw new MovimientoInvalidoException("Es un producto perecedero: indicá la fecha "
                        + "de vencimiento del lote que entra.");
            }
            String codigo = codigoLote == null || codigoLote.trim().isEmpty()
                    ? null : codigoLote.trim();
            Lote existente = buscarLote(producto.getIdproducto(), vencimiento, codigo);
            if (existente != null) {
                existente.setCantidad(existente.getCantidad() + cantidad);
                return existente;
            }
            Lote nuevo = new Lote();
            nuevo.setProductoIdproducto(producto);
            nuevo.setVencimiento(vencimiento);
            nuevo.setCodigo(codigo);
            nuevo.setCantidad(cantidad);
            em.persist(nuevo);
            return nuevo;
        }

        if (AJUSTE.equals(tipo)) {
            if (idLote == null) {
                throw new MovimientoInvalidoException("En un producto perecedero el ajuste se hace "
                        + "sobre un lote: elegí cuál.");
            }
            Lote elegido = loteDelProducto(producto, idLote);
            elegido.setCantidad(cantidad);
            return elegido;
        }

        if (idLote != null) {
            Lote elegido = loteDelProducto(producto, idLote);
            if (elegido.getCantidad() < cantidad) {
                throw new MovimientoInvalidoException("El lote tiene " + elegido.getCantidad()
                        + " " + producto.getUnidad() + " y se intentan retirar " + cantidad + ".");
            }
            elegido.setCantidad(elegido.getCantidad() - cantidad);
            return elegido;
        }

        Lote primero = null;
        int restante = cantidad;
        for (Lote candidato : lotesConStock(producto.getIdproducto())) {
            if (restante <= 0) {
                break;
            }
            int tomado = Math.min(candidato.getCantidad(), restante);
            candidato.setCantidad(candidato.getCantidad() - tomado);
            restante = restante - tomado;
            if (primero == null) {
                primero = candidato;
            }
        }
        if (restante > 0) {
            throw new MovimientoInvalidoException("Stock insuficiente en los lotes de "
                    + producto.getNombre() + ": faltan " + restante + " " + producto.getUnidad()
                    + ".");
        }
        return primero;
    }

    private Lote loteDelProducto(Producto producto, Integer idLote)
            throws MovimientoInvalidoException {
        Lote lote = em.find(Lote.class, idLote);
        if (lote == null
                || !lote.getProductoIdproducto().getIdproducto().equals(producto.getIdproducto())) {
            throw new MovimientoInvalidoException("El lote indicado no pertenece a "
                    + producto.getNombre() + ".");
        }
        return lote;
    }

    private Lote buscarLote(Integer idProducto, Date vencimiento, String codigo) {
        String jpql = "SELECT l FROM Lote l WHERE l.productoIdproducto.idproducto = :id "
                + "AND l.vencimiento = :vencimiento AND "
                + (codigo == null ? "l.codigo IS NULL" : "l.codigo = :codigo");
        TypedQuery<Lote> query = em.createQuery(jpql, Lote.class)
                .setParameter("id", idProducto)
                .setParameter("vencimiento", vencimiento, TemporalType.DATE);
        if (codigo != null) {
            query.setParameter("codigo", codigo);
        }
        List<Lote> lista = query.setMaxResults(1).getResultList();
        return lista.isEmpty() ? null : lista.get(0);
    }

    private List<Lote> lotesConStock(Integer idProducto) {
        return em.createQuery(
                "SELECT l FROM Lote l WHERE l.productoIdproducto.idproducto = :id AND l.cantidad > 0 "
                + "ORDER BY l.vencimiento, l.idlote", Lote.class)
                .setParameter("id", idProducto).getResultList();
    }

    private int sumaDeLotes(Integer idProducto) {
        Long suma = em.createQuery(
                "SELECT SUM(l.cantidad) FROM Lote l WHERE l.productoIdproducto.idproducto = :id",
                Long.class).setParameter("id", idProducto).getSingleResult();
        return suma == null ? 0 : suma.intValue();
    }

    public List<MovimientoStock> findUltimos(int cantidad, Integer idNegocio) {
        return getEntityManager().createQuery(
                "SELECT m FROM MovimientoStock m WHERE m.negocioIdnegocio.idnegocio = :idNegocio "
                + "ORDER BY m.fecha DESC, m.idmovimiento DESC", MovimientoStock.class)
                .setParameter("idNegocio", idNegocio).setMaxResults(cantidad).getResultList();
    }

    public List<MovimientoStock> buscar(Integer idProducto, String tipo, Integer idNegocio) {
        StringBuilder jpql = new StringBuilder(
                "SELECT m FROM MovimientoStock m WHERE m.negocioIdnegocio.idnegocio = :idNegocio");
        if (idProducto != null) {
            jpql.append(" AND m.productoIdproducto.idproducto = :idProducto");
        }
        if (tipo != null && !tipo.trim().isEmpty()) {
            jpql.append(" AND m.tipo = :tipo");
        }
        jpql.append(" ORDER BY m.fecha DESC, m.idmovimiento DESC");
        TypedQuery<MovimientoStock> query =
                getEntityManager().createQuery(jpql.toString(), MovimientoStock.class)
                .setParameter("idNegocio", idNegocio);
        if (idProducto != null) {
            query.setParameter("idProducto", idProducto);
        }
        if (tipo != null && !tipo.trim().isEmpty()) {
            query.setParameter("tipo", tipo);
        }
        return query.setMaxResults(200).getResultList();
    }

    public List<Object[]> findMasVendidos(int cantidad, Integer idNegocio) {
        return getEntityManager().createQuery(
                "SELECT m.productoIdproducto, SUM(m.cantidad) FROM MovimientoStock m "
                + "WHERE m.negocioIdnegocio.idnegocio = :idNegocio AND m.tipo = :tipo "
                + "GROUP BY m.productoIdproducto ORDER BY SUM(m.cantidad) DESC", Object[].class)
                .setParameter("idNegocio", idNegocio)
                .setParameter("tipo", SALIDA).setMaxResults(cantidad).getResultList();
    }

    public List<Object[]> findComprasPorProveedor(Integer idNegocio) {
        return getEntityManager().createQuery(
                "SELECT m.proveedorIdproveedor, COUNT(m), SUM(m.cantidad) FROM MovimientoStock m "
                + "WHERE m.negocioIdnegocio.idnegocio = :idNegocio AND m.tipo = :tipo "
                + "AND m.proveedorIdproveedor IS NOT NULL "
                + "GROUP BY m.proveedorIdproveedor ORDER BY SUM(m.cantidad) DESC", Object[].class)
                .setParameter("idNegocio", idNegocio).setParameter("tipo", ENTRADA).getResultList();
    }

}
