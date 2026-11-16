package sessions;

import entities.Proveedor;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ProveedorFacade extends AbstractFacade<Proveedor> {

    @PersistenceContext(unitName = "AppInventarioPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProveedorFacade() {
        super(Proveedor.class);
    }

    public Proveedor findEnNegocio(Integer id, Integer idNegocio) {
        Proveedor proveedor = find(id);
        if (proveedor == null || !proveedor.getNegocioIdnegocio().getIdnegocio().equals(idNegocio)) {
            return null;
        }
        return proveedor;
    }

    public List<Proveedor> findOrdenados(Integer idNegocio) {
        return getEntityManager().createQuery(
                "SELECT p FROM Proveedor p WHERE p.negocioIdnegocio.idnegocio = :idNegocio "
                + "ORDER BY p.nombre", Proveedor.class)
                .setParameter("idNegocio", idNegocio).getResultList();
    }

    public List<Proveedor> findConMasDeDosProductos(Integer idNegocio) {
        return getEntityManager().createQuery(
                "SELECT p FROM Proveedor p WHERE p.negocioIdnegocio.idnegocio = :idNegocio "
                + "AND SIZE(p.productoProveedorCollection) > 2 ORDER BY p.nombre", Proveedor.class)
                .setParameter("idNegocio", idNegocio).getResultList();
    }

    public List<Object[]> findConContadores(Integer idNegocio) {
        return getEntityManager().createQuery(
                "SELECT p, COUNT(pp) FROM Proveedor p LEFT JOIN p.productoProveedorCollection pp "
                + "WHERE p.negocioIdnegocio.idnegocio = :idNegocio "
                + "GROUP BY p.idproveedor, p.nombre, p.telefono, p.email ORDER BY p.nombre",
                Object[].class).setParameter("idNegocio", idNegocio).getResultList();
    }

    public boolean existeNombre(String nombre, Integer idExcluir, Integer idNegocio) {
        Long repetidos = getEntityManager().createQuery(
                "SELECT COUNT(p) FROM Proveedor p WHERE LOWER(p.nombre) = :nombre "
                + "AND p.negocioIdnegocio.idnegocio = :idNegocio AND p.idproveedor <> :id",
                Long.class)
                .setParameter("nombre", nombre.trim().toLowerCase())
                .setParameter("idNegocio", idNegocio)
                .setParameter("id", idExcluir == null ? 0 : idExcluir).getSingleResult();
        return repetidos > 0;
    }

    public boolean hasRelations(Integer id) {
        Long articulos = getEntityManager().createQuery(
                "SELECT COUNT(pp) FROM ProductoProveedor pp "
                + "WHERE pp.productoProveedorPK.proveedorIdproveedor = :id",
                Long.class).setParameter("id", id).getSingleResult();
        Long movimientos = getEntityManager().createQuery(
                "SELECT COUNT(m) FROM MovimientoStock m WHERE m.proveedorIdproveedor.idproveedor = :id",
                Long.class).setParameter("id", id).getSingleResult();
        return articulos > 0 || movimientos > 0;
    }

}
