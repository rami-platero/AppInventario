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
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "movimiento_stock", catalog = "inventariodb", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MovimientoStock.findAll", query = "SELECT m FROM MovimientoStock m"),
    @NamedQuery(name = "MovimientoStock.findByIdmovimiento", query = "SELECT m FROM MovimientoStock m WHERE m.idmovimiento = :idmovimiento"),
    @NamedQuery(name = "MovimientoStock.findByTipo", query = "SELECT m FROM MovimientoStock m WHERE m.tipo = :tipo"),
    @NamedQuery(name = "MovimientoStock.findByCantidad", query = "SELECT m FROM MovimientoStock m WHERE m.cantidad = :cantidad"),
    @NamedQuery(name = "MovimientoStock.findByStockResultante", query = "SELECT m FROM MovimientoStock m WHERE m.stockResultante = :stockResultante"),
    @NamedQuery(name = "MovimientoStock.findByMotivo", query = "SELECT m FROM MovimientoStock m WHERE m.motivo = :motivo"),
    @NamedQuery(name = "MovimientoStock.findByFecha", query = "SELECT m FROM MovimientoStock m WHERE m.fecha = :fecha")})
public class MovimientoStock implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idmovimiento", nullable = false)
    private Integer idmovimiento;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "tipo", nullable = false, length = 10)
    private String tipo;
    @Basic(optional = false)
    @NotNull
    @Column(name = "cantidad", nullable = false)
    private int cantidad;
    @Basic(optional = false)
    @NotNull
    @Column(name = "stock_resultante", nullable = false)
    private int stockResultante;
    @Size(max = 200)
    @Column(name = "motivo", length = 200)
    private String motivo;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @JoinColumn(name = "lote_idlote", referencedColumnName = "idlote")
    @ManyToOne
    private Lote loteIdlote;
    @JoinColumn(name = "producto_idproducto", referencedColumnName = "idproducto", nullable = false)
    @ManyToOne(optional = false)
    private Producto productoIdproducto;
    @JoinColumn(name = "proveedor_idproveedor", referencedColumnName = "idproveedor")
    @ManyToOne
    private Proveedor proveedorIdproveedor;

    public MovimientoStock() {
    }

    public MovimientoStock(Integer idmovimiento) {
        this.idmovimiento = idmovimiento;
    }

    public MovimientoStock(Integer idmovimiento, String tipo, int cantidad, int stockResultante, Date fecha) {
        this.idmovimiento = idmovimiento;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.stockResultante = stockResultante;
        this.fecha = fecha;
    }

    public Integer getIdmovimiento() {
        return idmovimiento;
    }

    public void setIdmovimiento(Integer idmovimiento) {
        this.idmovimiento = idmovimiento;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getStockResultante() {
        return stockResultante;
    }

    public void setStockResultante(int stockResultante) {
        this.stockResultante = stockResultante;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Lote getLoteIdlote() {
        return loteIdlote;
    }

    public void setLoteIdlote(Lote loteIdlote) {
        this.loteIdlote = loteIdlote;
    }

    public Producto getProductoIdproducto() {
        return productoIdproducto;
    }

    public void setProductoIdproducto(Producto productoIdproducto) {
        this.productoIdproducto = productoIdproducto;
    }

    public Proveedor getProveedorIdproveedor() {
        return proveedorIdproveedor;
    }

    public void setProveedorIdproveedor(Proveedor proveedorIdproveedor) {
        this.proveedorIdproveedor = proveedorIdproveedor;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idmovimiento != null ? idmovimiento.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MovimientoStock)) {
            return false;
        }
        MovimientoStock other = (MovimientoStock) object;
        if ((this.idmovimiento == null && other.idmovimiento != null) || (this.idmovimiento != null && !this.idmovimiento.equals(other.idmovimiento))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.MovimientoStock[ idmovimiento=" + idmovimiento + " ]";
    }

}
