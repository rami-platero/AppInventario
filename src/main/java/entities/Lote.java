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
@Table(name = "lote", catalog = "inventariodb", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Lote.findAll", query = "SELECT l FROM Lote l"),
    @NamedQuery(name = "Lote.findByIdlote", query = "SELECT l FROM Lote l WHERE l.idlote = :idlote"),
    @NamedQuery(name = "Lote.findByCodigo", query = "SELECT l FROM Lote l WHERE l.codigo = :codigo"),
    @NamedQuery(name = "Lote.findByVencimiento", query = "SELECT l FROM Lote l WHERE l.vencimiento = :vencimiento"),
    @NamedQuery(name = "Lote.findByCantidad", query = "SELECT l FROM Lote l WHERE l.cantidad = :cantidad")})
public class Lote implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idlote", nullable = false)
    private Integer idlote;
    @Size(max = 30)
    @Column(name = "codigo", length = 30)
    private String codigo;
    @Basic(optional = false)
    @NotNull
    @Column(name = "vencimiento", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date vencimiento;
    @Basic(optional = false)
    @NotNull
    @Column(name = "cantidad", nullable = false)
    private int cantidad;
    @OneToMany(mappedBy = "loteIdlote")
    private Collection<MovimientoStock> movimientoStockCollection;
    @JoinColumn(name = "producto_idproducto", referencedColumnName = "idproducto", nullable = false)
    @ManyToOne(optional = false)
    private Producto productoIdproducto;

    public Lote() {
    }

    public Lote(Integer idlote) {
        this.idlote = idlote;
    }

    public Lote(Integer idlote, Date vencimiento, int cantidad) {
        this.idlote = idlote;
        this.vencimiento = vencimiento;
        this.cantidad = cantidad;
    }

    public Integer getIdlote() {
        return idlote;
    }

    public void setIdlote(Integer idlote) {
        this.idlote = idlote;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Date getVencimiento() {
        return vencimiento;
    }

    public void setVencimiento(Date vencimiento) {
        this.vencimiento = vencimiento;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    @XmlTransient
    public Collection<MovimientoStock> getMovimientoStockCollection() {
        return movimientoStockCollection;
    }

    public void setMovimientoStockCollection(Collection<MovimientoStock> movimientoStockCollection) {
        this.movimientoStockCollection = movimientoStockCollection;
    }

    public Producto getProductoIdproducto() {
        return productoIdproducto;
    }

    public void setProductoIdproducto(Producto productoIdproducto) {
        this.productoIdproducto = productoIdproducto;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idlote != null ? idlote.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Lote)) {
            return false;
        }
        Lote other = (Lote) object;
        if ((this.idlote == null && other.idlote != null) || (this.idlote != null && !this.idlote.equals(other.idlote))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Lote[ idlote=" + idlote + " ]";
    }

}
