package sessions;

import entities.Lote;
import java.io.Serializable;
import java.math.BigDecimal;

public class SugerenciaLote implements Serializable {

    private static final long serialVersionUID = 1L;

    private Lote lote;
    private int dias;
    private String estado;
    private String etiquetaEstado;
    private String descripcionDias;
    private String nivel;
    private String titulo;
    private String detalle;
    private String fundamento;
    private Integer descuento;
    private Integer unidadesEnRiesgo;
    private BigDecimal valorEnRiesgo;
    private Integer proyeccion;
    private RitmoVenta ritmo;

    public SugerenciaLote() {
    }

    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
    }

    public int getDias() {
        return dias;
    }

    public void setDias(int dias) {
        this.dias = dias;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEtiquetaEstado() {
        return etiquetaEstado;
    }

    public void setEtiquetaEstado(String etiquetaEstado) {
        this.etiquetaEstado = etiquetaEstado;
    }

    public String getDescripcionDias() {
        return descripcionDias;
    }

    public void setDescripcionDias(String descripcionDias) {
        this.descripcionDias = descripcionDias;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getFundamento() {
        return fundamento;
    }

    public void setFundamento(String fundamento) {
        this.fundamento = fundamento;
    }

    public Integer getDescuento() {
        return descuento;
    }

    public void setDescuento(Integer descuento) {
        this.descuento = descuento;
    }

    public Integer getUnidadesEnRiesgo() {
        return unidadesEnRiesgo;
    }

    public void setUnidadesEnRiesgo(Integer unidadesEnRiesgo) {
        this.unidadesEnRiesgo = unidadesEnRiesgo;
    }

    public BigDecimal getValorEnRiesgo() {
        return valorEnRiesgo;
    }

    public void setValorEnRiesgo(BigDecimal valorEnRiesgo) {
        this.valorEnRiesgo = valorEnRiesgo;
    }

    public Integer getProyeccion() {
        return proyeccion;
    }

    public void setProyeccion(Integer proyeccion) {
        this.proyeccion = proyeccion;
    }

    public RitmoVenta getRitmo() {
        return ritmo;
    }

    public void setRitmo(RitmoVenta ritmo) {
        this.ritmo = ritmo;
    }

}
