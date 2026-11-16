package sessions;

import entities.ProductoProveedor;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ProductoProveedorFacade extends AbstractFacade<ProductoProveedor> {

    @PersistenceContext(unitName = "AppInventarioPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProductoProveedorFacade() {
        super(ProductoProveedor.class);
    }

    public List<ProductoProveedor> findByProducto(Integer idProducto) {
        return getEntityManager().createQuery(
                "SELECT pp FROM ProductoProveedor pp "
                + "WHERE pp.productoProveedorPK.productoIdproducto = :id "
                + "ORDER BY pp.precioCompra", ProductoProveedor.class)
                .setParameter("id", idProducto).getResultList();
    }

    public List<ProductoProveedor> findByProveedor(Integer idProveedor) {
        return getEntityManager().createQuery(
                "SELECT pp FROM ProductoProveedor pp "
                + "WHERE pp.productoProveedorPK.proveedorIdproveedor = :id "
                + "ORDER BY pp.producto.nombre", ProductoProveedor.class)
                .setParameter("id", idProveedor).getResultList();
    }

    public boolean existe(Integer idProducto, Integer idProveedor) {
        Long repetidos = getEntityManager().createQuery(
                "SELECT COUNT(pp) FROM ProductoProveedor pp "
                + "WHERE pp.productoProveedorPK.productoIdproducto = :producto "
                + "AND pp.productoProveedorPK.proveedorIdproveedor = :proveedor", Long.class)
                .setParameter("producto", idProducto)
                .setParameter("proveedor", idProveedor).getSingleResult();
        return repetidos > 0;
    }

    public ProductoProveedor findMejorProveedor(Integer idProducto) {
        List<ProductoProveedor> lista = getEntityManager().createQuery(
                "SELECT pp FROM ProductoProveedor pp "
                + "WHERE pp.productoProveedorPK.productoIdproducto = :id "
                + "ORDER BY pp.precioCompra, pp.productoProveedorPK.proveedorIdproveedor",
                ProductoProveedor.class)
                .setParameter("id", idProducto).setMaxResults(1).getResultList();
        return lista.isEmpty() ? null : lista.get(0);
    }

}
