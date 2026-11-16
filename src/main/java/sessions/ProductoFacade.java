package sessions;

import entities.Producto;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class ProductoFacade extends AbstractFacade<Producto> {

    @PersistenceContext(unitName = "AppInventarioPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProductoFacade() {
        super(Producto.class);
    }

    public Producto findEnNegocio(Integer id, Integer idNegocio) {
        Producto producto = find(id);
        if (producto == null || !producto.getNegocioIdnegocio().getIdnegocio().equals(idNegocio)) {
            return null;
        }
        return producto;
    }

    public List<Producto> findOrdenados(Integer idNegocio) {
        return getEntityManager().createQuery(
                "SELECT p FROM Producto p WHERE p.negocioIdnegocio.idnegocio = :idNegocio "
                + "ORDER BY p.nombre", Producto.class)
                .setParameter("idNegocio", idNegocio).getResultList();
    }

    public List<Producto> findBajoMinimo(Integer idNegocio) {
        return getEntityManager().createQuery(
                "SELECT p FROM Producto p WHERE p.negocioIdnegocio.idnegocio = :idNegocio "
                + "AND p.stock <= p.stockMinimo ORDER BY p.stock, p.nombre", Producto.class)
                .setParameter("idNegocio", idNegocio).getResultList();
    }

    public List<Producto> buscar(String texto, Integer idCategoria, String estado, String orden,
            Integer idNegocio) {
        StringBuilder jpql = new StringBuilder(
                "SELECT p FROM Producto p WHERE p.negocioIdnegocio.idnegocio = :idNegocio");
        if (texto != null && !texto.trim().isEmpty()) {
            jpql.append(" AND (LOWER(p.nombre) LIKE :texto OR LOWER(p.codigo) LIKE :texto)");
        }
        if (idCategoria != null) {
            jpql.append(" AND p.categoriaIdcategoria.idcategoria = :idCategoria");
        }
        if ("agotado".equals(estado)) {
            jpql.append(" AND p.stock = 0");
        } else if ("reponer".equals(estado)) {
            jpql.append(" AND p.stock <= p.stockMinimo");
        } else if ("normal".equals(estado)) {
            jpql.append(" AND p.stock > p.stockMinimo");
        }
        if ("stock".equals(orden)) {
            jpql.append(" ORDER BY p.stock, p.nombre");
        } else if ("precio".equals(orden)) {
            jpql.append(" ORDER BY p.precio DESC, p.nombre");
        } else if ("categoria".equals(orden)) {
            jpql.append(" ORDER BY p.categoriaIdcategoria.nombre, p.nombre");
        } else {
            jpql.append(" ORDER BY p.nombre");
        }
        TypedQuery<Producto> query = getEntityManager().createQuery(jpql.toString(), Producto.class)
                .setParameter("idNegocio", idNegocio);
        if (texto != null && !texto.trim().isEmpty()) {
            query.setParameter("texto", "%" + texto.trim().toLowerCase() + "%");
        }
        if (idCategoria != null) {
            query.setParameter("idCategoria", idCategoria);
        }
        return query.getResultList();
    }

    public long countBajoMinimo(Integer idNegocio) {
        return getEntityManager().createQuery(
                "SELECT COUNT(p) FROM Producto p WHERE p.negocioIdnegocio.idnegocio = :idNegocio "
                + "AND p.stock <= p.stockMinimo", Long.class)
                .setParameter("idNegocio", idNegocio).getSingleResult();
    }

    public long countAgotados(Integer idNegocio) {
        return getEntityManager().createQuery(
                "SELECT COUNT(p) FROM Producto p WHERE p.negocioIdnegocio.idnegocio = :idNegocio "
                + "AND p.stock = 0", Long.class)
                .setParameter("idNegocio", idNegocio).getSingleResult();
    }

    public long totalUnidades(Integer idNegocio) {
        Long total = getEntityManager().createQuery(
                "SELECT SUM(p.stock) FROM Producto p WHERE p.negocioIdnegocio.idnegocio = :idNegocio",
                Long.class).setParameter("idNegocio", idNegocio).getSingleResult();
        return total == null ? 0L : total;
    }

    public BigDecimal valorInventario(Integer idNegocio) {
        BigDecimal valor = getEntityManager().createQuery(
                "SELECT SUM(p.stock * p.precio) FROM Producto p "
                + "WHERE p.negocioIdnegocio.idnegocio = :idNegocio", BigDecimal.class)
                .setParameter("idNegocio", idNegocio).getSingleResult();
        return valor == null ? BigDecimal.ZERO : valor;
    }

    public List<Producto> findSinProveedor(Integer idNegocio) {
        return getEntityManager().createQuery(
                "SELECT p FROM Producto p WHERE p.negocioIdnegocio.idnegocio = :idNegocio "
                + "AND p.productoProveedorCollection IS EMPTY ORDER BY p.nombre", Producto.class)
                .setParameter("idNegocio", idNegocio).getResultList();
    }

    public boolean existeCodigo(String codigo, Integer idExcluir, Integer idNegocio) {
        Long repetidos = getEntityManager().createQuery(
                "SELECT COUNT(p) FROM Producto p WHERE LOWER(p.codigo) = :codigo "
                + "AND p.negocioIdnegocio.idnegocio = :idNegocio AND p.idproducto <> :id",
                Long.class)
                .setParameter("codigo", codigo.trim().toLowerCase())
                .setParameter("idNegocio", idNegocio)
                .setParameter("id", idExcluir == null ? 0 : idExcluir).getSingleResult();
        return repetidos > 0;
    }

    public void editarDatos(Producto datos) {
        Producto actual = getEntityManager().find(Producto.class, datos.getIdproducto());
        actual.setCodigo(datos.getCodigo());
        actual.setNombre(datos.getNombre());
        actual.setDescripcion(datos.getDescripcion());
        actual.setUnidad(datos.getUnidad());
        actual.setStockMinimo(datos.getStockMinimo());
        actual.setPrecio(datos.getPrecio());
        actual.setCategoriaIdcategoria(datos.getCategoriaIdcategoria());
    }

    public boolean hasRelations(Integer id) {
        Long movimientos = getEntityManager().createQuery(
                "SELECT COUNT(m) FROM MovimientoStock m WHERE m.productoIdproducto.idproducto = :id",
                Long.class).setParameter("id", id).getSingleResult();
        Long lotes = getEntityManager().createQuery(
                "SELECT COUNT(l) FROM Lote l WHERE l.productoIdproducto.idproducto = :id",
                Long.class).setParameter("id", id).getSingleResult();
        return movimientos > 0 || lotes > 0;
    }

    public void eliminar(Producto producto) {
        getEntityManager().createQuery(
                "DELETE FROM ProductoProveedor pp "
                + "WHERE pp.productoProveedorPK.productoIdproducto = :id")
                .setParameter("id", producto.getIdproducto()).executeUpdate();
        remove(producto);
    }

}
