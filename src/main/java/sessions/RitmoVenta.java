package sessions;

import java.io.Serializable;

public class RitmoVenta implements Serializable {

    private static final long serialVersionUID = 1L;

    private int salidas;
    private int unidades;
    private int diasHistorial;
    private Double porDia;
    private String confianza;
    private String motivo;

    public RitmoVenta() {
    }

    public int getSalidas() {
        return salidas;
    }

    public void setSalidas(int salidas) {
        this.salidas = salidas;
    }

    public int getUnidades() {
        return unidades;
    }

    public void setUnidades(int unidades) {
        this.unidades = unidades;
    }

    public int getDiasHistorial() {
        return diasHistorial;
    }

    public void setDiasHistorial(int diasHistorial) {
        this.diasHistorial = diasHistorial;
    }

    public Double getPorDia() {
        return porDia;
    }

    public void setPorDia(Double porDia) {
        this.porDia = porDia;
    }

    public String getConfianza() {
        return confianza;
    }

    public void setConfianza(String confianza) {
        this.confianza = confianza;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getEtiquetaConfianza() {
        if ("alta".equals(confianza)) {
            return "confianza alta";
        }
        if ("media".equals(confianza)) {
            return "confianza media";
        }
        if ("baja".equals(confianza)) {
            return "confianza baja";
        }
        return "sin datos suficientes";
    }

}
