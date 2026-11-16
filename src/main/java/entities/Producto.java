package entities;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;

@Entity
@Table(name = "producto", catalog = "inventariodb", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Producto.findAll", query = "SELECT p FROM Producto p"),
    @NamedQuery(name = "Producto.findByIdproducto", query = "SELECT p FROM Producto p WHERE p.idproducto = :idproducto"),
    @NamedQuery(name = "Producto.findByCodigo", query = "SELECT p FROM Producto p WHERE p.codigo = :codigo"),
    @NamedQuery(name = "Producto.findByNombre", query = "SELECT p FROM Producto p WHERE p.nombre = :nombre"),
    @NamedQuery(name = "Producto.findByDescripcion", query = "SELECT p FROM Producto p WHERE p.descripcion = :descripcion"),
    @NamedQuery(name = "Producto.findByUnidad", query = "SELECT p FROM Producto p WHERE p.unidad = :unidad"),
    @NamedQuery(name = "Producto.findByStock", query = "SELECT p FROM Producto p WHERE p.stock = :stock"),
    @NamedQuery(name = "Producto.findByStockMinimo", query = "SELECT p FROM Producto p WHERE p.stockMinimo = :stockMinimo"),
    @NamedQuery(name = "Producto.findByPrecio", query = "SELECT p FROM Producto p WHERE p.precio = :precio"),
    @NamedQuery(name = "Producto.findByPerecedero", query = "SELECT p FROM Producto p WHERE p.perecedero = :perecedero")})
public class Producto implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idproducto", nullable = false)
    private Integer idproducto;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "codigo", nullable = false, length = 20)
    private String codigo;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 60)
    @Column(name = "nombre", nullable = false, length = 60)
    private String nombre;
    @Size(max = 200)
    @Column(name = "descripcion", length = 200)
    private String descripcion;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "unidad", nullable = false, length = 15)
    private String unidad;
    @Basic(optional = false)
    @NotNull
    @Column(name = "stock", nullable = false)
    private int stock;
    @Basic(optional = false)
    @NotNull
    @Column(name = "stock_minimo", nullable = false)
    private int stockMinimo;
    @Basic(optional = false)
    @NotNull
    @Column(name = "precio", nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;
    @Basic(optional = false)
    @NotNull
    @Column(name = "perecedero", nullable = false)
    private boolean perecedero;
    @OneToMany(mappedBy = "producto")
    private Collection<ProductoProveedor> productoProveedorCollection;
    @OneToMany(mappedBy = "productoIdproducto")
    private Collection<Lote> loteCollection;
    @OneToMany(mappedBy = "productoIdproducto")
    private Collection<MovimientoStock> movimientoStockCollection;
    @JoinColumn(name = "categoria_idcategoria", referencedColumnName = "idcategoria", nullable = false)
    @ManyToOne(optional = false)
    private Categoria categoriaIdcategoria;

    public Producto() {
    }

    public Producto(Integer idproducto) {
        this.idproducto = idproducto;
    }

    public Producto(Integer idproducto, String codigo, String nombre, String unidad, int stock, int stockMinimo, BigDecimal precio, boolean perecedero) {
        this.idproducto = idproducto;
        this.codigo = codigo;
        this.nombre = nombre;
        this.unidad = unidad;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.precio = precio;
        this.perecedero = perecedero;
    }

    public Integer getIdproducto() {
        return idproducto;
    }

    public void setIdproducto(Integer idproducto) {
        this.idproducto = idproducto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public boolean getPerecedero() {
        return perecedero;
    }

    public void setPerecedero(boolean perecedero) {
        this.perecedero = perecedero;
    }

    @XmlTransient
    public Collection<ProductoProveedor> getProductoProveedorCollection() {
        return productoProveedorCollection;
    }

    public void setProductoProveedorCollection(Collection<ProductoProveedor> productoProveedorCollection) {
        this.productoProveedorCollection = productoProveedorCollection;
    }

    @XmlTransient
    public Collection<Lote> getLoteCollection() {
        return loteCollection;
    }

    public void setLoteCollection(Collection<Lote> loteCollection) {
        this.loteCollection = loteCollection;
    }

    @XmlTransient
    public Collection<MovimientoStock> getMovimientoStockCollection() {
        return movimientoStockCollection;
    }

    public void setMovimientoStockCollection(Collection<MovimientoStock> movimientoStockCollection) {
        this.movimientoStockCollection = movimientoStockCollection;
    }

    public Categoria getCategoriaIdcategoria() {
        return categoriaIdcategoria;
    }

    public void setCategoriaIdcategoria(Categoria categoriaIdcategoria) {
        this.categoriaIdcategoria = categoriaIdcategoria;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idproducto != null ? idproducto.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Producto)) {
            return false;
        }
        Producto other = (Producto) object;
        if ((this.idproducto == null && other.idproducto != null) || (this.idproducto != null && !this.idproducto.equals(other.idproducto))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Producto[ idproducto=" + idproducto + " ]";
    }

}
