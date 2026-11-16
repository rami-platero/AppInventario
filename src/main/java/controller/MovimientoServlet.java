package controller;

import entities.Lote;
import entities.Producto;
import entities.ProductoProveedor;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sessions.LoteFacade;
import sessions.MovimientoInvalidoException;
import sessions.MovimientoStockFacade;
import sessions.ProductoProveedorFacade;
import sessions.ProveedorFacade;
import sessions.SesionActual;

@WebServlet(name = "MovimientoServlet", urlPatterns = {
    "/ListarMovimientos", "/RegistrarMovimiento",
    "/Vencimientos", "/DescartarLote", "/Reponer"})
public class MovimientoServlet extends BaseControlador {

    private static final int LARGO_CODIGO_LOTE = 30;
    private static final int LARGO_MOTIVO = 200;

    @EJB
    private MovimientoStockFacade movimientoF;
    @EJB
    private ProveedorFacade proveedorF;
    @EJB
    private ProductoProveedorFacade productoProveedorF;

    private void procesar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();
        String url = "/WEB-INF/views/ListarMovimientos.jsp";
        boolean post = "POST".equals(request.getMethod());
        Integer idNegocio = SesionActual.idNegocioActivo(request);

        switch (path) {
            case "/ListarMovimientos":
                request.setAttribute("movimientos", movimientoF.buscar(
                        entero(request, "producto"), request.getParameter("tipo"), idNegocio));
                request.setAttribute("productos", productoF.findOrdenados(idNegocio));
                url = "/WEB-INF/views/ListarMovimientos.jsp";
                break;

            case "/RegistrarMovimiento":
                if (post) {
                    List<String> errores = validarMovimiento(request, idNegocio);
                    if (errores.isEmpty()) {
                        try {
                            movimientoF.registrar(
                                    entero(request, "producto"),
                                    request.getParameter("tipo"),
                                    entero(request, "cantidad"),
                                    entero(request, "proveedor"),
                                    parametroTexto(request, "motivo"),
                                    fecha(request, "vencimiento"),
                                    parametroTexto(request, "codigoLote"),
                                    entero(request, "lote"));
                            response.sendRedirect(request.getContextPath() + "/ListarMovimientos");
                            return;
                        } catch (MovimientoInvalidoException e) {
                            errores.add(e.getMessage());
                        }
                    }
                    request.setAttribute("errores", errores);
                }
                request.setAttribute("productos", productoF.findOrdenados(idNegocio));
                request.setAttribute("proveedores", proveedorF.findOrdenados(idNegocio));
                request.setAttribute("lotes", loteF.findConStock(idNegocio));
                url = "/WEB-INF/views/FormMovimiento.jsp";
                break;

            case "/Vencimientos":
                request.setAttribute("sugerencias",
                        loteF.analizarVencimientos(LoteFacade.DIAS_AVISO, idNegocio));
                request.setAttribute("unidadesPorVencer",
                        loteF.unidadesPorVencer(LoteFacade.DIAS_AVISO, idNegocio));
                request.setAttribute("diasAviso", LoteFacade.DIAS_AVISO);
                url = "/WEB-INF/views/Vencimientos.jsp";
                break;

            case "/DescartarLote": {
                String error = "";
                Integer id = entero(request, "id");
                if (post && id != null) {
                    Lote lote = loteF.findEnNegocio(id, idNegocio);
                    if (lote != null && lote.getCantidad() > 0) {
                        try {
                            movimientoF.registrar(
                                    lote.getProductoIdproducto().getIdproducto(),
                                    MovimientoStockFacade.SALIDA,
                                    lote.getCantidad(), null,
                                    "Merma: lote vencido o descartado",
                                    null, null, lote.getIdlote());
                        } catch (MovimientoInvalidoException e) {
                            error = "?error=" + codificar(e.getMessage());
                        }
                    }
                }
                response.sendRedirect(request.getContextPath() + "/Vencimientos" + error);
                return;
            }

            case "/Reponer": {
                List<Producto> bajoMinimo = productoF.findBajoMinimo(idNegocio);
                request.setAttribute("productos", bajoMinimo);
                request.setAttribute("mejores", mejoresProveedores(bajoMinimo));
                url = "/WEB-INF/views/Reponer.jsp";
                break;
            }

            default:
                break;
        }
        despachar(request, response, url);
    }

    private List<String> validarMovimiento(HttpServletRequest request, Integer idNegocio) {
        List<String> errores = new ArrayList<>();

        Integer idProducto = entero(request, "producto");
        if (idProducto == null || productoF.findEnNegocio(idProducto, idNegocio) == null) {
            errores.add("Hay que elegir un producto de la lista.");
        }

        String tipo = request.getParameter("tipo");
        if (!MovimientoStockFacade.ENTRADA.equals(tipo)
                && !MovimientoStockFacade.SALIDA.equals(tipo)
                && !MovimientoStockFacade.AJUSTE.equals(tipo)) {
            errores.add("Hay que elegir el tipo de movimiento.");
        }

        validarEntero(request, "cantidad", "La cantidad", errores);

        String fecha = request.getParameter("vencimiento");
        if (fecha != null && !fecha.trim().isEmpty() && fecha(request, "vencimiento") == null) {
            errores.add("La fecha de vencimiento no es válida.");
        }

        String codigoLote = parametroTexto(request, "codigoLote");
        if (codigoLote != null && codigoLote.length() > LARGO_CODIGO_LOTE) {
            errores.add("El código del lote no puede tener más de " + LARGO_CODIGO_LOTE
                    + " caracteres.");
        }

        Integer idProveedor = entero(request, "proveedor");
        if (idProveedor != null && proveedorF.findEnNegocio(idProveedor, idNegocio) == null) {
            errores.add("El proveedor elegido no es válido.");
        }

        Integer idLote = entero(request, "lote");
        if (idLote != null && loteF.findEnNegocio(idLote, idNegocio) == null) {
            errores.add("El lote elegido no es válido.");
        }

        String motivo = parametroTexto(request, "motivo");
        if (motivo != null && motivo.length() > LARGO_MOTIVO) {
            errores.add("El motivo no puede tener más de " + LARGO_MOTIVO + " caracteres.");
        }
        return errores;
    }

    private Map<Integer, ProductoProveedor> mejoresProveedores(List<Producto> productos) {
        Map<Integer, ProductoProveedor> mejores = new HashMap<>();
        for (Producto producto : productos) {
            ProductoProveedor mejor = productoProveedorF.findMejorProveedor(producto.getIdproducto());
            if (mejor != null) {
                mejores.put(producto.getIdproducto(), mejor);
            }
        }
        return mejores;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        procesar(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        procesar(request, response);
    }
}
