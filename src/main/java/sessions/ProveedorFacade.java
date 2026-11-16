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

    public List<Proveedor> findOrdenados() {
        return getEntityManager().createQuery(
                "SELECT p FROM Proveedor p ORDER BY p.nombre", Proveedor.class).getResultList();
    }

    public List<Proveedor> findConMasDeDosProductos() {
        return getEntityManager().createQuery(
                "SELECT p FROM Proveedor p WHERE SIZE(p.productoProveedorCollection) > 2 "
                + "ORDER BY p.nombre", Proveedor.class).getResultList();
    }

    public List<Object[]> findConContadores() {
        return getEntityManager().createQuery(
                "SELECT p, COUNT(pp) FROM Proveedor p LEFT JOIN p.productoProveedorCollection pp "
                + "GROUP BY p.idproveedor, p.nombre, p.telefono, p.email ORDER BY p.nombre",
                Object[].class).getResultList();
    }

    public boolean existeNombre(String nombre, Integer idExcluir) {
        Long repetidos = getEntityManager().createQuery(
                "SELECT COUNT(p) FROM Proveedor p WHERE LOWER(p.nombre) = :nombre "
                + "AND (:id IS NULL OR p.idproveedor <> :id)", Long.class)
                .setParameter("nombre", nombre.trim().toLowerCase())
                .setParameter("id", idExcluir).getSingleResult();
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
