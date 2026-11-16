package entities;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Embeddable
public class ProductoProveedorPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "producto_idproducto", nullable = false)
    private int productoIdproducto;
    @Basic(optional = false)
    @NotNull
    @Column(name = "proveedor_idproveedor", nullable = false)
    private int proveedorIdproveedor;

    public ProductoProveedorPK() {
    }

    public ProductoProveedorPK(int productoIdproducto, int proveedorIdproveedor) {
        this.productoIdproducto = productoIdproducto;
        this.proveedorIdproveedor = proveedorIdproveedor;
    }

    public int getProductoIdproducto() {
        return productoIdproducto;
    }

    public void setProductoIdproducto(int productoIdproducto) {
        this.productoIdproducto = productoIdproducto;
    }

    public int getProveedorIdproveedor() {
        return proveedorIdproveedor;
    }

    public void setProveedorIdproveedor(int proveedorIdproveedor) {
        this.proveedorIdproveedor = proveedorIdproveedor;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) productoIdproducto;
        hash += (int) proveedorIdproveedor;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ProductoProveedorPK)) {
            return false;
        }
        ProductoProveedorPK other = (ProductoProveedorPK) object;
        if (this.productoIdproducto != other.productoIdproducto) {
            return false;
        }
        if (this.proveedorIdproveedor != other.proveedorIdproveedor) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.ProductoProveedorPK[ productoIdproducto=" + productoIdproducto + ", proveedorIdproveedor=" + proveedorIdproveedor + " ]";
    }

}
