package controller;

import entities.Categoria;
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
import sessions.CategoriaFacade;
import sessions.SesionActual;

@WebServlet(name = "CategoriaServlet", urlPatterns = {
    "/ListarCategorias", "/AgregarCategoria", "/EditarCategoria", "/EliminarCategoria"})
public class CategoriaServlet extends BaseControlador {

    private static final int LARGO_NOMBRE = 45;

    @EJB
    private CategoriaFacade categoriaF;

    private void procesar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();
        String url = "/WEB-INF/views/ListarCategorias.jsp";
        boolean post = "POST".equals(request.getMethod());
        Integer idNegocio = SesionActual.idNegocioActivo(request);

        switch (path) {
            case "/ListarCategorias": {
                Map<Integer, Long> alertas = new HashMap<>();
                for (Object[] fila : categoriaF.countAlertasPorCategoria(idNegocio)) {
                    alertas.put((Integer) fila[0], (Long) fila[1]);
                }
                request.setAttribute("categorias", categoriaF.findConCantidadDeProductos(idNegocio));
                request.setAttribute("alertas", alertas);
                url = "/WEB-INF/views/ListarCategorias.jsp";
                break;
            }

            case "/AgregarCategoria":
                if (post) {
                    List<String> errores = validarCategoria(request, null, idNegocio);
                    if (errores.isEmpty()) {
                        Categoria nueva = new Categoria();
                        nueva.setNombre(parametroTexto(request, "nombre"));
                        nueva.setNegocioIdnegocio(SesionActual.negocioActivo(request));
                        categoriaF.create(nueva);
                        response.sendRedirect(request.getContextPath() + "/ListarCategorias");
                        return;
                    }
                    request.setAttribute("errores", errores);
                }
                url = "/WEB-INF/views/FormCategoria.jsp";
                break;

            case "/EditarCategoria": {
                Integer id = entero(request, "id");
                Categoria categoria = id == null ? null : categoriaF.findEnNegocio(id, idNegocio);
                if (categoria == null) {
                    response.sendRedirect(request.getContextPath() + "/ListarCategorias?error="
                            + codificar("Esa categoría ya no existe."));
                    return;
                }
                if (post) {
                    List<String> errores = validarCategoria(request, id, idNegocio);
                    if (errores.isEmpty()) {
                        categoria.setNombre(parametroTexto(request, "nombre"));
                        categoriaF.edit(categoria);
                        response.sendRedirect(request.getContextPath() + "/ListarCategorias");
                        return;
                    }
                    request.setAttribute("errores", errores);
                }
                request.setAttribute("categoria", categoria);
                url = "/WEB-INF/views/FormCategoria.jsp";
                break;
            }

            case "/EliminarCategoria": {
                String error = "";
                Integer id = entero(request, "id");
                if (post && id != null) {
                    Categoria categoria = categoriaF.findEnNegocio(id, idNegocio);
                    if (categoria == null) {
                        error = "?error=" + codificar("Esa categoría ya no existe.");
                    } else if (categoriaF.hasRelations(id)) {
                        error = "?error=" + codificar("No se puede eliminar la categoría: "
                                + "tiene productos asociados.");
                    } else {
                        categoriaF.remove(categoria);
                    }
                }
                response.sendRedirect(request.getContextPath() + "/ListarCategorias" + error);
                return;
            }

            default:
                break;
        }
        despachar(request, response, url);
    }

    private List<String> validarCategoria(HttpServletRequest request, Integer idActual,
            Integer idNegocio) {
        List<String> errores = new ArrayList<>();
        String nombre = parametroTexto(request, "nombre");
        if (nombre == null) {
            errores.add("El nombre es obligatorio.");
        } else if (nombre.length() > LARGO_NOMBRE) {
            errores.add("El nombre no puede tener más de " + LARGO_NOMBRE + " caracteres.");
        } else if (categoriaF.existeNombre(nombre, idActual, idNegocio)) {
            errores.add("Ya existe una categoría llamada " + nombre + ".");
        }
        return errores;
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
