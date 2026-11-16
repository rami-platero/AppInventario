package controller;

import entities.Membresia;
import entities.Negocio;
import entities.Usuario;
import jakarta.ejb.EJB;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import sessions.MembresiaFacade;
import sessions.SesionActual;

@WebFilter(urlPatterns = "/*")
public class SesionNegocioFiltro extends HttpFilter {

    private static final Set<String> PUBLICAS = Set.of(
            "/IniciarSesion", "/Registrarse", "/CerrarSesion");

    private static final Set<String> SIN_NEGOCIO = Set.of(
            "/Negocios", "/CrearNegocio");

    private static final Set<String> SOLO_PROPIETARIO = Set.of(
            "/AgregarProducto", "/EditarProducto", "/EliminarProducto",
            "/AgregarProveedorProducto", "/EliminarProveedorProducto",
            "/AgregarCategoria", "/EditarCategoria", "/EliminarCategoria",
            "/AgregarProveedor", "/EditarProveedor", "/EliminarProveedor",
            "/Empleados", "/AgregarEmpleado", "/CambiarRolEmpleado", "/EliminarEmpleado");

    @EJB
    private MembresiaFacade membresiaF;

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        String path = request.getServletPath();

        if (path.startsWith("/css/") || PUBLICAS.contains(path)) {
            chain.doFilter(request, response);
            return;
        }

        Usuario usuario = SesionActual.usuario(request);
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/IniciarSesion");
            return;
        }

        if (SIN_NEGOCIO.contains(path)) {
            chain.doFilter(request, response);
            return;
        }

        Negocio negocio = SesionActual.negocioActivo(request);
        if (negocio == null) {
            response.sendRedirect(request.getContextPath() + "/Negocios");
            return;
        }

        Membresia membresia = membresiaF.findRol(usuario.getIdusuario(), negocio.getIdnegocio());
        if (membresia == null) {
            SesionActual.salirDelNegocio(request);
            response.sendRedirect(request.getContextPath() + "/Negocios?error="
                    + codificar("Ya no pertenecés a " + negocio.getNombre() + "."));
            return;
        }
        if (!membresia.getRol().equals(SesionActual.rolActivo(request))) {
            SesionActual.elegirNegocio(request, negocio, membresia.getRol());
        }

        if (SOLO_PROPIETARIO.contains(path)
                && !MembresiaFacade.PROPIETARIO.equals(membresia.getRol())) {
            response.sendRedirect(request.getContextPath() + "/Panel?error="
                    + codificar("Esa acción es sólo para el propietario del negocio."));
            return;
        }

        chain.doFilter(request, response);
    }

    private String codificar(String texto) {
        return URLEncoder.encode(texto, StandardCharsets.UTF_8);
    }

}
