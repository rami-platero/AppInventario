package entities;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "negocio", catalog = "inventariodb", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Negocio.findAll", query = "SELECT n FROM Negocio n"),
    @NamedQuery(name = "Negocio.findByIdnegocio", query = "SELECT n FROM Negocio n WHERE n.idnegocio = :idnegocio"),
    @NamedQuery(name = "Negocio.findByNombre", query = "SELECT n FROM Negocio n WHERE n.nombre = :nombre"),
    @NamedQuery(name = "Negocio.findByFechaCreacion", query = "SELECT n FROM Negocio n WHERE n.fechaCreacion = :fechaCreacion")})
public class Negocio implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idnegocio", nullable = false)
    private Integer idnegocio;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 60)
    @Column(name = "nombre", nullable = false, length = 60)
    private String nombre;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_creacion", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;
    @OneToMany(mappedBy = "negocio")
    private Collection<Membresia> membresiaCollection;
    @OneToMany(mappedBy = "negocioIdnegocio")
    private Collection<Categoria> categoriaCollection;
    @OneToMany(mappedBy = "negocioIdnegocio")
    private Collection<Proveedor> proveedorCollection;
    @OneToMany(mappedBy = "negocioIdnegocio")
    private Collection<Producto> productoCollection;
    @OneToMany(mappedBy = "negocioIdnegocio")
    private Collection<MovimientoStock> movimientoStockCollection;

    public Negocio() {
    }

    public Negocio(Integer idnegocio) {
        this.idnegocio = idnegocio;
    }

    public Negocio(Integer idnegocio, String nombre, Date fechaCreacion) {
        this.idnegocio = idnegocio;
        this.nombre = nombre;
        this.fechaCreacion = fechaCreacion;
    }

    public Integer getIdnegocio() {
        return idnegocio;
    }

    public void setIdnegocio(Integer idnegocio) {
        this.idnegocio = idnegocio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @XmlTransient
    public Collection<Membresia> getMembresiaCollection() {
        return membresiaCollection;
    }

    public void setMembresiaCollection(Collection<Membresia> membresiaCollection) {
        this.membresiaCollection = membresiaCollection;
    }

    @XmlTransient
    public Collection<Categoria> getCategoriaCollection() {
        return categoriaCollection;
    }

    public void setCategoriaCollection(Collection<Categoria> categoriaCollection) {
        this.categoriaCollection = categoriaCollection;
    }

    @XmlTransient
    public Collection<Proveedor> getProveedorCollection() {
        return proveedorCollection;
    }

    public void setProveedorCollection(Collection<Proveedor> proveedorCollection) {
        this.proveedorCollection = proveedorCollection;
    }

    @XmlTransient
    public Collection<Producto> getProductoCollection() {
        return productoCollection;
    }

    public void setProductoCollection(Collection<Producto> productoCollection) {
        this.productoCollection = productoCollection;
    }

    @XmlTransient
    public Collection<MovimientoStock> getMovimientoStockCollection() {
        return movimientoStockCollection;
    }

    public void setMovimientoStockCollection(Collection<MovimientoStock> movimientoStockCollection) {
        this.movimientoStockCollection = movimientoStockCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idnegocio != null ? idnegocio.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Negocio)) {
            return false;
        }
        Negocio other = (Negocio) object;
        if ((this.idnegocio == null && other.idnegocio != null) || (this.idnegocio != null && !this.idnegocio.equals(other.idnegocio))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Negocio[ idnegocio=" + idnegocio + " ]";
    }

}
