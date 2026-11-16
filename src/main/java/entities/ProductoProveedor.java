package entities;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "producto_proveedor", catalog = "inventariodb", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ProductoProveedor.findAll", query = "SELECT p FROM ProductoProveedor p"),
    @NamedQuery(name = "ProductoProveedor.findByProductoIdproducto", query = "SELECT p FROM ProductoProveedor p WHERE p.productoProveedorPK.productoIdproducto = :productoIdproducto"),
    @NamedQuery(name = "ProductoProveedor.findByProveedorIdproveedor", query = "SELECT p FROM ProductoProveedor p WHERE p.productoProveedorPK.proveedorIdproveedor = :proveedorIdproveedor"),
    @NamedQuery(name = "ProductoProveedor.findByPrecioCompra", query = "SELECT p FROM ProductoProveedor p WHERE p.precioCompra = :precioCompra"),
    @NamedQuery(name = "ProductoProveedor.findByDiasEntrega", query = "SELECT p FROM ProductoProveedor p WHERE p.diasEntrega = :diasEntrega"),
    @NamedQuery(name = "ProductoProveedor.findByCodigoProveedor", query = "SELECT p FROM ProductoProveedor p WHERE p.codigoProveedor = :codigoProveedor")})
public class ProductoProveedor implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ProductoProveedorPK productoProveedorPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "precio_compra", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioCompra;
    @Column(name = "dias_entrega")
    private Integer diasEntrega;
    @Size(max = 30)
    @Column(name = "codigo_proveedor", length = 30)
    private String codigoProveedor;
    @JoinColumn(name = "producto_idproducto", referencedColumnName = "idproducto", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Producto producto;
    @JoinColumn(name = "proveedor_idproveedor", referencedColumnName = "idproveedor", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Proveedor proveedor;

    public ProductoProveedor() {
    }

    public ProductoProveedor(ProductoProveedorPK productoProveedorPK) {
        this.productoProveedorPK = productoProveedorPK;
    }

    public ProductoProveedor(ProductoProveedorPK productoProveedorPK, BigDecimal precioCompra) {
        this.productoProveedorPK = productoProveedorPK;
        this.precioCompra = precioCompra;
    }

    public ProductoProveedor(int productoIdproducto, int proveedorIdproveedor) {
        this.productoProveedorPK = new ProductoProveedorPK(productoIdproducto, proveedorIdproveedor);
    }

    public ProductoProveedorPK getProductoProveedorPK() {
        return productoProveedorPK;
    }

    public void setProductoProveedorPK(ProductoProveedorPK productoProveedorPK) {
        this.productoProveedorPK = productoProveedorPK;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }

    public Integer getDiasEntrega() {
        return diasEntrega;
    }

    public void setDiasEntrega(Integer diasEntrega) {
        this.diasEntrega = diasEntrega;
    }

    public String getCodigoProveedor() {
        return codigoProveedor;
    }

    public void setCodigoProveedor(String codigoProveedor) {
        this.codigoProveedor = codigoProveedor;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (productoProveedorPK != null ? productoProveedorPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ProductoProveedor)) {
            return false;
        }
        ProductoProveedor other = (ProductoProveedor) object;
        if ((this.productoProveedorPK == null && other.productoProveedorPK != null) || (this.productoProveedorPK != null && !this.productoProveedorPK.equals(other.productoProveedorPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.ProductoProveedor[ productoProveedorPK=" + productoProveedorPK + " ]";
    }

}
