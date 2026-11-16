package controller;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import sessions.CategoriaFacade;
import sessions.MovimientoStockFacade;
import sessions.ProveedorFacade;
import sessions.SesionActual;

@WebServlet(name = "PanelServlet", urlPatterns = {"/Panel"})
public class PanelServlet extends BaseControlador {

    @EJB
    private CategoriaFacade categoriaF;
    @EJB
    private ProveedorFacade proveedorF;
    @EJB
    private MovimientoStockFacade movimientoF;

    private void procesar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Integer idNegocio = SesionActual.idNegocioActivo(request);

        request.setAttribute("cantidadProductos", productoF.findOrdenados(idNegocio).size());
        request.setAttribute("totalUnidades", productoF.totalUnidades(idNegocio));
        request.setAttribute("valorInventario", productoF.valorInventario(idNegocio));
        request.setAttribute("cantidadCategorias", categoriaF.findOrdenadas(idNegocio).size());
        request.setAttribute("cantidadProveedores", proveedorF.findOrdenados(idNegocio).size());
        request.setAttribute("reponer", productoF.findBajoMinimo(idNegocio));
        request.setAttribute("movimientos", movimientoF.findUltimos(8, idNegocio));

        despachar(request, response, "/WEB-INF/views/Panel.jsp");
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
