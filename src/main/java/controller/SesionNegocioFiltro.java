package controller;

import entities.Negocio;
import entities.Usuario;
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

        if (SOLO_PROPIETARIO.contains(path) && !SesionActual.esPropietario(request)) {
            response.sendRedirect(request.getContextPath() + "/Panel?error="
                    + URLEncoder.encode("Esa acción es sólo para el propietario del negocio.",
                            StandardCharsets.UTF_8));
            return;
        }

        chain.doFilter(request, response);
    }

}
