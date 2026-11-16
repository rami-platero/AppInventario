package sessions;

import entities.Lote;
import entities.Producto;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class LoteFacade extends AbstractFacade<Lote> {

    public static final int DIAS_AVISO = 30;
    public static final int DIAS_CRITICOS = 7;

    private static final long MILIS_POR_DIA = 86400000L;
    private static final int VENTANA_MAXIMA_DIAS = 90;
    private static final int MINIMO_SALIDAS = 3;
    private static final int MINIMO_DIAS_HISTORIAL = 7;
    private static final int DESCUENTO_MINIMO = 10;
    private static final int DESCUENTO_MAXIMO = 40;

    @PersistenceContext(unitName = "AppInventarioPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LoteFacade() {
        super(Lote.class);
    }

    public Lote findEnNegocio(Integer id, Integer idNegocio) {
        Lote lote = find(id);
        if (lote == null
                || !lote.getProductoIdproducto().getNegocioIdnegocio().getIdnegocio().equals(idNegocio)) {
            return null;
        }
        return lote;
    }

    public List<Lote> findByProducto(Integer idProducto) {
        return getEntityManager().createQuery(
                "SELECT l FROM Lote l WHERE l.productoIdproducto.idproducto = :id AND l.cantidad > 0 "
                + "ORDER BY l.vencimiento", Lote.class)
                .setParameter("id", idProducto).getResultList();
    }

    public List<Lote> findConStock(Integer idNegocio) {
        return getEntityManager().createQuery(
                "SELECT l FROM Lote l WHERE l.productoIdproducto.negocioIdnegocio.idnegocio = :idNegocio "
                + "AND l.cantidad > 0 ORDER BY l.productoIdproducto.nombre, l.vencimiento", Lote.class)
                .setParameter("idNegocio", idNegocio).getResultList();
    }

    public List<Lote> findPorVencer(int dias, Integer idNegocio) {
        Calendar limite = Calendar.getInstance();
        limite.add(Calendar.DAY_OF_MONTH, dias);
        return getEntityManager().createQuery(
                "SELECT l FROM Lote l WHERE l.productoIdproducto.negocioIdnegocio.idnegocio = :idNegocio "
                + "AND l.cantidad > 0 AND l.vencimiento <= :limite ORDER BY l.vencimiento", Lote.class)
                .setParameter("idNegocio", idNegocio)
                .setParameter("limite", limite.getTime(), TemporalType.DATE).getResultList();
    }

    public long countPorVencer(int dias, Integer idNegocio) {
        Calendar limite = Calendar.getInstance();
        limite.add(Calendar.DAY_OF_MONTH, dias);
        return getEntityManager().createQuery(
                "SELECT COUNT(l) FROM Lote l "
                + "WHERE l.productoIdproducto.negocioIdnegocio.idnegocio = :idNegocio "
                + "AND l.cantidad > 0 AND l.vencimiento <= :limite", Long.class)
                .setParameter("idNegocio", idNegocio)
                .setParameter("limite", limite.getTime(), TemporalType.DATE).getSingleResult();
    }

    public long countVencidos(Integer idNegocio) {
        return getEntityManager().createQuery(
                "SELECT COUNT(l) FROM Lote l "
                + "WHERE l.productoIdproducto.negocioIdnegocio.idnegocio = :idNegocio "
                + "AND l.cantidad > 0 AND l.vencimiento < :hoy", Long.class)
                .setParameter("idNegocio", idNegocio)
                .setParameter("hoy", new Date(), TemporalType.DATE).getSingleResult();
    }

    public long unidadesPorVencer(int dias, Integer idNegocio) {
        Calendar limite = Calendar.getInstance();
        limite.add(Calendar.DAY_OF_MONTH, dias);
        Long suma = getEntityManager().createQuery(
                "SELECT SUM(l.cantidad) FROM Lote l "
                + "WHERE l.productoIdproducto.negocioIdnegocio.idnegocio = :idNegocio "
                + "AND l.cantidad > 0 AND l.vencimiento <= :limite", Long.class)
                .setParameter("idNegocio", idNegocio)
                .setParameter("limite", limite.getTime(), TemporalType.DATE).getSingleResult();
        return suma == null ? 0L : suma;
    }

    public List<SugerenciaLote> analizarVencimientos(int dias, Integer idNegocio) {
        List<SugerenciaLote> resultado = new ArrayList<>();
        Map<Integer, RitmoVenta> cache = new HashMap<>();
        for (Lote lote : findPorVencer(dias, idNegocio)) {
            Producto producto = lote.getProductoIdproducto();
            RitmoVenta ritmo = cache.get(producto.getIdproducto());
            if (ritmo == null) {
                ritmo = calcularRitmo(producto.getIdproducto());
                cache.put(producto.getIdproducto(), ritmo);
            }
            resultado.add(sugerir(lote, producto, ritmo));
        }
        return resultado;
    }

    public RitmoVenta calcularRitmo(Integer idProducto) {
        RitmoVenta ritmo = new RitmoVenta();
        Object masViejo = getEntityManager().createQuery(
                "SELECT MIN(m.fecha) FROM MovimientoStock m "
                + "WHERE m.productoIdproducto.idproducto = :id")
                .setParameter("id", idProducto).getSingleResult();

        if (masViejo == null) {
            ritmo.setConfianza("ninguna");
            ritmo.setMotivo("El producto todavía no tiene movimientos registrados.");
            return ritmo;
        }

        long ahora = System.currentTimeMillis();
        long inicio = Math.max(((Date) masViejo).getTime(),
                ahora - VENTANA_MAXIMA_DIAS * MILIS_POR_DIA);
        int diasObservados = (int) Math.max(1,
                Math.round((ahora - inicio) / (double) MILIS_POR_DIA));

        Object[] fila = getEntityManager().createQuery(
                "SELECT COUNT(m), SUM(m.cantidad) FROM MovimientoStock m "
                + "WHERE m.productoIdproducto.idproducto = :id AND m.tipo = :tipo "
                + "AND m.fecha >= :inicio", Object[].class)
                .setParameter("id", idProducto)
                .setParameter("tipo", MovimientoStockFacade.SALIDA)
                .setParameter("inicio", new Date(inicio), TemporalType.TIMESTAMP)
                .getSingleResult();

        int salidas = ((Number) fila[0]).intValue();
        int unidades = fila[1] == null ? 0 : ((Number) fila[1]).intValue();

        ritmo.setSalidas(salidas);
        ritmo.setUnidades(unidades);
        ritmo.setDiasHistorial(diasObservados);

        if (salidas < MINIMO_SALIDAS || diasObservados < MINIMO_DIAS_HISTORIAL) {
            ritmo.setConfianza("ninguna");
            ritmo.setMotivo("Hacen falta al menos " + MINIMO_SALIDAS + " salidas y "
                    + MINIMO_DIAS_HISTORIAL + " días de historial para estimar un ritmo de venta. Van "
                    + salidas + " salidas en " + diasObservados + " días.");
            return ritmo;
        }

        ritmo.setPorDia(unidades / (double) diasObservados);
        if (salidas >= 12 && diasObservados >= 21) {
            ritmo.setConfianza("alta");
        } else if (salidas >= 6) {
            ritmo.setConfianza("media");
        } else {
            ritmo.setConfianza("baja");
        }
        ritmo.setMotivo(unidades + " unidades en " + salidas + " salidas durante "
                + diasObservados + " días de historial.");
        return ritmo;
    }

    private SugerenciaLote sugerir(Lote lote, Producto producto, RitmoVenta ritmo) {
        SugerenciaLote sugerencia = new SugerenciaLote();
        sugerencia.setLote(lote);
        sugerencia.setRitmo(ritmo);

        int dias = diasHasta(lote.getVencimiento());
        sugerencia.setDias(dias);
        sugerencia.setDescripcionDias(describirDias(dias));

        if (dias < 0) {
            sugerencia.setEstado("VENCIDO");
            sugerencia.setEtiquetaEstado("Vencido");
        } else if (dias <= DIAS_CRITICOS) {
            sugerencia.setEstado("CRITICO");
            sugerencia.setEtiquetaEstado("Vence ya");
        } else {
            sugerencia.setEstado("PRONTO");
            sugerencia.setEtiquetaEstado("Por vencer");
        }

        BigDecimal precio = producto.getPrecio() == null ? BigDecimal.ZERO : producto.getPrecio();

        if (dias < 0) {
            sugerencia.setNivel("baja");
            sugerencia.setTitulo("Dar de baja");
            sugerencia.setUnidadesEnRiesgo(lote.getCantidad());
            sugerencia.setValorEnRiesgo(precio.multiply(BigDecimal.valueOf(lote.getCantidad())));
            sugerencia.setDetalle("El lote está vencido: " + lote.getCantidad() + " "
                    + producto.getUnidad() + " no se pueden vender. Registrá la merma para que el "
                    + "stock refleje la realidad.");
            sugerencia.setFundamento("Dato cierto: la fecha de vencimiento ya pasó.");
            return sugerencia;
        }

        if (ritmo.getPorDia() == null) {
            sugerencia.setNivel("sin-datos");
            if (dias <= DIAS_CRITICOS) {
                sugerencia.setTitulo("Revisar a mano");
                sugerencia.setDetalle("Vence en " + dias + " días y hay " + lote.getCantidad() + " "
                        + producto.getUnidad() + ". No hay historial suficiente para saber si se van "
                        + "a vender: decidilo con tu criterio.");
            } else {
                sugerencia.setTitulo("Sin datos para proyectar");
                sugerencia.setDetalle("Todavía no se puede estimar si este lote llega a venderse "
                        + "antes de vencer.");
            }
            sugerencia.setFundamento(ritmo.getMotivo());
            return sugerencia;
        }

        double proyeccion = ritmo.getPorDia() * dias;
        int enRiesgo = (int) Math.max(Math.round(lote.getCantidad() - proyeccion), 0);
        sugerencia.setProyeccion((int) Math.round(proyeccion));
        sugerencia.setUnidadesEnRiesgo(enRiesgo);
        sugerencia.setValorEnRiesgo(precio.multiply(BigDecimal.valueOf(enRiesgo)));
        sugerencia.setFundamento(ritmo.getMotivo());

        if (enRiesgo == 0) {
            sugerencia.setNivel("ninguna");
            sugerencia.setTitulo("No hace falta promo");
            sugerencia.setDetalle("Al ritmo observado se venderían unas "
                    + Math.round(proyeccion) + " " + producto.getUnidad()
                    + " antes del vencimiento, más que las " + lote.getCantidad() + " del lote.");
            return sugerencia;
        }

        double necesarioPorDia = lote.getCantidad() / (double) Math.max(dias, 1);
        double aceleracion = necesarioPorDia / ritmo.getPorDia();

        if (aceleracion > 4) {
            sugerencia.setNivel("liquidar");
            sugerencia.setTitulo("Liquidar");
        } else if (aceleracion > 1.25) {
            sugerencia.setNivel("promocionar");
            sugerencia.setTitulo("Promocionar");
        } else {
            sugerencia.setNivel("vigilar");
            sugerencia.setTitulo("Vigilar");
        }

        if (!"vigilar".equals(sugerencia.getNivel())) {
            int descuento = (int) (Math.round(DESCUENTO_MINIMO * aceleracion / 5.0) * 5);
            sugerencia.setDescuento(Math.min(Math.max(descuento, DESCUENTO_MINIMO), DESCUENTO_MAXIMO));
        }

        sugerencia.setDetalle("Quedan " + lote.getCantidad() + " " + producto.getUnidad()
                + " y faltan " + dias + " días. Al ritmo actual se venderían unas "
                + Math.round(proyeccion) + ": sobrarían " + enRiesgo
                + ". Para vaciarlo habría que vender "
                + String.format("%.1f", aceleracion) + " veces más rápido.");
        return sugerencia;
    }

    private int diasHasta(Date vencimiento) {
        Calendar hoy = Calendar.getInstance();
        aMedianoche(hoy);
        Calendar objetivo = Calendar.getInstance();
        objetivo.setTime(vencimiento);
        aMedianoche(objetivo);
        return (int) Math.round(
                (objetivo.getTimeInMillis() - hoy.getTimeInMillis()) / (double) MILIS_POR_DIA);
    }

    private void aMedianoche(Calendar calendario) {
        calendario.set(Calendar.HOUR_OF_DAY, 0);
        calendario.set(Calendar.MINUTE, 0);
        calendario.set(Calendar.SECOND, 0);
        calendario.set(Calendar.MILLISECOND, 0);
    }

    private String describirDias(int dias) {
        if (dias < -1) {
            return "venció hace " + Math.abs(dias) + " días";
        }
        if (dias == -1) {
            return "venció ayer";
        }
        if (dias == 0) {
            return "vence hoy";
        }
        if (dias == 1) {
            return "vence mañana";
        }
        return "vence en " + dias + " días";
    }

}
