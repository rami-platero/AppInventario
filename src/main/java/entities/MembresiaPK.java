package entities;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Embeddable
public class MembresiaPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "usuario_idusuario", nullable = false)
    private int usuarioIdusuario;
    @Basic(optional = false)
    @NotNull
    @Column(name = "negocio_idnegocio", nullable = false)
    private int negocioIdnegocio;

    public MembresiaPK() {
    }

    public MembresiaPK(int usuarioIdusuario, int negocioIdnegocio) {
        this.usuarioIdusuario = usuarioIdusuario;
        this.negocioIdnegocio = negocioIdnegocio;
    }

    public int getUsuarioIdusuario() {
        return usuarioIdusuario;
    }

    public void setUsuarioIdusuario(int usuarioIdusuario) {
        this.usuarioIdusuario = usuarioIdusuario;
    }

    public int getNegocioIdnegocio() {
        return negocioIdnegocio;
    }

    public void setNegocioIdnegocio(int negocioIdnegocio) {
        this.negocioIdnegocio = negocioIdnegocio;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) usuarioIdusuario;
        hash += (int) negocioIdnegocio;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MembresiaPK)) {
            return false;
        }
        MembresiaPK other = (MembresiaPK) object;
        if (this.usuarioIdusuario != other.usuarioIdusuario) {
            return false;
        }
        if (this.negocioIdnegocio != other.negocioIdnegocio) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.MembresiaPK[ usuarioIdusuario=" + usuarioIdusuario + ", negocioIdnegocio=" + negocioIdnegocio + " ]";
    }

}
