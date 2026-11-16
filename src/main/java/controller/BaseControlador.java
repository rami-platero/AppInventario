package controller;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import sessions.LoteFacade;
import sessions.ProductoFacade;
import sessions.SesionActual;

public abstract class BaseControlador extends HttpServlet {

    @EJB
    protected ProductoFacade productoF;
    @EJB
    protected LoteFacade loteF;

    protected void despachar(HttpServletRequest request, HttpServletResponse response, String url)
            throws ServletException, IOException {
        Integer idNegocio = SesionActual.idNegocioActivo(request);
        if (idNegocio != null) {
            cargarContadores(request, idNegocio);
        }
        request.getRequestDispatcher(url).forward(request, response);
    }

    private void cargarContadores(HttpServletRequest request, Integer idNegocio) {
        request.setAttribute("contadorReponer", productoF.countBajoMinimo(idNegocio));
        request.setAttribute("contadorAgotados", productoF.countAgotados(idNegocio));
        request.setAttribute("contadorPorVencer",
                loteF.countPorVencer(LoteFacade.DIAS_AVISO, idNegocio));
        request.setAttribute("contadorVencidos", loteF.countVencidos(idNegocio));
    }

    protected Integer entero(HttpServletRequest request, String nombre) {
        String valor = request.getParameter(nombre);
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(valor.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected Date fecha(HttpServletRequest request, String nombre) {
        String valor = request.getParameter(nombre);
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        try {
            return java.sql.Date.valueOf(valor.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    protected String parametroTexto(HttpServletRequest request, String nombre) {
        String valor = request.getParameter(nombre);
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        return valor.trim();
    }

    protected String codificar(String texto) {
        return URLEncoder.encode(texto, StandardCharsets.UTF_8);
    }

    protected void validarEntero(HttpServletRequest request, String campo, String rotulo,
            List<String> errores) {
        String valor = request.getParameter(campo);
        if (valor == null || valor.trim().isEmpty()) {
            errores.add(rotulo + " es obligatorio.");
            return;
        }
        Integer numero = entero(request, campo);
        if (numero == null) {
            errores.add(rotulo + " tiene que ser un número entero.");
        } else if (numero < 0) {
            errores.add(rotulo + " no puede ser negativo.");
        }
    }

    protected void validarDecimal(HttpServletRequest request, String campo, String rotulo,
            List<String> errores) {
        String valor = request.getParameter(campo);
        if (valor == null || valor.trim().isEmpty()) {
            errores.add(rotulo + " es obligatorio.");
            return;
        }
        try {
            if (new BigDecimal(valor.trim()).signum() < 0) {
                errores.add(rotulo + " no puede ser negativo.");
            }
        } catch (NumberFormatException e) {
            errores.add(rotulo + " tiene que ser un número.");
        }
    }
}
