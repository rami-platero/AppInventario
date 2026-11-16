package controller;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import sessions.MovimientoStockFacade;
import sessions.ProveedorFacade;
import sessions.SesionActual;

@WebServlet(name = "ConsultaServlet", urlPatterns = {
    "/MasVendidos", "/ComprasPorProveedor", "/SinProveedor", "/ProveedoresTop"})
public class ConsultaServlet extends BaseControlador {

    @EJB
    private MovimientoStockFacade movimientoF;
    @EJB
    private ProveedorFacade proveedorF;

    private void procesar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();
        String url = "/WEB-INF/views/MasVendidos.jsp";
        Integer idNegocio = SesionActual.idNegocioActivo(request);

        switch (path) {
            case "/MasVendidos":
                request.setAttribute("filas", movimientoF.findMasVendidos(10, idNegocio));
                url = "/WEB-INF/views/MasVendidos.jsp";
                break;

            case "/ComprasPorProveedor":
                request.setAttribute("filas", movimientoF.findComprasPorProveedor(idNegocio));
                url = "/WEB-INF/views/ComprasPorProveedor.jsp";
                break;

            case "/SinProveedor":
                request.setAttribute("productos", productoF.findSinProveedor(idNegocio));
                url = "/WEB-INF/views/SinProveedor.jsp";
                break;

            case "/ProveedoresTop":
                request.setAttribute("proveedores", proveedorF.findConMasDeDosProductos(idNegocio));
                url = "/WEB-INF/views/ProveedoresTop.jsp";
                break;

            default:
                break;
        }
        despachar(request, response, url);
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
