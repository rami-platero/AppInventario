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

@Entity
@Table(name = "membresia", catalog = "inventariodb", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Membresia.findAll", query = "SELECT m FROM Membresia m"),
    @NamedQuery(name = "Membresia.findByUsuarioIdusuario", query = "SELECT m FROM Membresia m WHERE m.membresiaPK.usuarioIdusuario = :usuarioIdusuario"),
    @NamedQuery(name = "Membresia.findByNegocioIdnegocio", query = "SELECT m FROM Membresia m WHERE m.membresiaPK.negocioIdnegocio = :negocioIdnegocio"),
    @NamedQuery(name = "Membresia.findByRol", query = "SELECT m FROM Membresia m WHERE m.rol = :rol")})
public class Membresia implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected MembresiaPK membresiaPK;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 12)
    @Column(name = "rol", nullable = false, length = 12)
    private String rol;
    @JoinColumn(name = "usuario_idusuario", referencedColumnName = "idusuario", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Usuario usuario;
    @JoinColumn(name = "negocio_idnegocio", referencedColumnName = "idnegocio", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Negocio negocio;

    public Membresia() {
    }

    public Membresia(MembresiaPK membresiaPK) {
        this.membresiaPK = membresiaPK;
    }

    public Membresia(MembresiaPK membresiaPK, String rol) {
        this.membresiaPK = membresiaPK;
        this.rol = rol;
    }

    public Membresia(int usuarioIdusuario, int negocioIdnegocio) {
        this.membresiaPK = new MembresiaPK(usuarioIdusuario, negocioIdnegocio);
    }

    public MembresiaPK getMembresiaPK() {
        return membresiaPK;
    }

    public void setMembresiaPK(MembresiaPK membresiaPK) {
        this.membresiaPK = membresiaPK;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Negocio getNegocio() {
        return negocio;
    }

    public void setNegocio(Negocio negocio) {
        this.negocio = negocio;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (membresiaPK != null ? membresiaPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Membresia)) {
            return false;
        }
        Membresia other = (Membresia) object;
        if ((this.membresiaPK == null && other.membresiaPK != null) || (this.membresiaPK != null && !this.membresiaPK.equals(other.membresiaPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Membresia[ membresiaPK=" + membresiaPK + " ]";
    }

}
