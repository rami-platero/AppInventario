package sessions;

import entities.Categoria;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class CategoriaFacade extends AbstractFacade<Categoria> {

    @PersistenceContext(unitName = "AppInventarioPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CategoriaFacade() {
        super(Categoria.class);
    }

    public Categoria findEnNegocio(Integer id, Integer idNegocio) {
        Categoria categoria = find(id);
        if (categoria == null || !categoria.getNegocioIdnegocio().getIdnegocio().equals(idNegocio)) {
            return null;
        }
        return categoria;
    }

    public List<Categoria> findOrdenadas(Integer idNegocio) {
        return getEntityManager().createQuery(
                "SELECT c FROM Categoria c WHERE c.negocioIdnegocio.idnegocio = :idNegocio "
                + "ORDER BY c.nombre", Categoria.class)
                .setParameter("idNegocio", idNegocio).getResultList();
    }

    public List<Object[]> findConCantidadDeProductos(Integer idNegocio) {
        return getEntityManager().createQuery(
                "SELECT c, COUNT(p) FROM Categoria c LEFT JOIN c.productoCollection p "
                + "WHERE c.negocioIdnegocio.idnegocio = :idNegocio "
                + "GROUP BY c.idcategoria, c.nombre ORDER BY c.nombre",
                Object[].class).setParameter("idNegocio", idNegocio).getResultList();
    }

    public List<Object[]> countAlertasPorCategoria(Integer idNegocio) {
        return getEntityManager().createQuery(
                "SELECT p.categoriaIdcategoria.idcategoria, COUNT(p) FROM Producto p "
                + "WHERE p.negocioIdnegocio.idnegocio = :idNegocio AND p.stock <= p.stockMinimo "
                + "GROUP BY p.categoriaIdcategoria.idcategoria",
                Object[].class).setParameter("idNegocio", idNegocio).getResultList();
    }

    public boolean existeNombre(String nombre, Integer idExcluir, Integer idNegocio) {
        Long repetidas = getEntityManager().createQuery(
                "SELECT COUNT(c) FROM Categoria c WHERE LOWER(c.nombre) = :nombre "
                + "AND c.negocioIdnegocio.idnegocio = :idNegocio AND c.idcategoria <> :id",
                Long.class)
                .setParameter("nombre", nombre.trim().toLowerCase())
                .setParameter("idNegocio", idNegocio)
                .setParameter("id", idExcluir == null ? 0 : idExcluir).getSingleResult();
        return repetidas > 0;
    }

    public boolean hasRelations(Integer id) {
        Long productos = getEntityManager().createQuery(
                "SELECT COUNT(p) FROM Producto p WHERE p.categoriaIdcategoria.idcategoria = :id",
                Long.class).setParameter("id", id).getSingleResult();
        return productos > 0;
    }

}
