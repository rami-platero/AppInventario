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

    public List<Categoria> findOrdenadas() {
        return getEntityManager().createQuery(
                "SELECT c FROM Categoria c ORDER BY c.nombre", Categoria.class).getResultList();
    }

    public List<Object[]> findConCantidadDeProductos() {
        return getEntityManager().createQuery(
                "SELECT c, COUNT(p) FROM Categoria c LEFT JOIN c.productoCollection p "
                + "GROUP BY c.idcategoria, c.nombre ORDER BY c.nombre",
                Object[].class).getResultList();
    }

    public List<Object[]> countAlertasPorCategoria() {
        return getEntityManager().createQuery(
                "SELECT p.categoriaIdcategoria.idcategoria, COUNT(p) FROM Producto p "
                + "WHERE p.stock <= p.stockMinimo GROUP BY p.categoriaIdcategoria.idcategoria",
                Object[].class).getResultList();
    }

    public boolean existeNombre(String nombre, Integer idExcluir) {
        Long repetidas = getEntityManager().createQuery(
                "SELECT COUNT(c) FROM Categoria c WHERE LOWER(c.nombre) = :nombre "
                + "AND (:id IS NULL OR c.idcategoria <> :id)", Long.class)
                .setParameter("nombre", nombre.trim().toLowerCase())
                .setParameter("id", idExcluir).getSingleResult();
        return repetidas > 0;
    }

    public boolean hasRelations(Integer id) {
        Long productos = getEntityManager().createQuery(
                "SELECT COUNT(p) FROM Producto p WHERE p.categoriaIdcategoria.idcategoria = :id",
                Long.class).setParameter("id", id).getSingleResult();
        return productos > 0;
    }

}
