package controller;

import entities.Proveedor;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import sessions.ProveedorFacade;
import sessions.SesionActual;

@WebServlet(name = "ProveedorServlet", urlPatterns = {
    "/ListarProveedores", "/AgregarProveedor", "/EditarProveedor", "/EliminarProveedor"})
public class ProveedorServlet extends BaseControlador {

    private static final int LARGO_NOMBRE = 45;
    private static final int LARGO_TELEFONO = 20;
    private static final int LARGO_EMAIL = 60;

    @EJB
    private ProveedorFacade proveedorF;

    private void procesar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();
        String url = "/WEB-INF/views/ListarProveedores.jsp";
        boolean post = "POST".equals(request.getMethod());
        Integer idNegocio = SesionActual.idNegocioActivo(request);

        switch (path) {
            case "/ListarProveedores":
                request.setAttribute("proveedores", proveedorF.findConContadores(idNegocio));
                url = "/WEB-INF/views/ListarProveedores.jsp";
                break;

            case "/AgregarProveedor":
                if (post) {
                    List<String> errores = validarProveedor(request, null, idNegocio);
                    if (errores.isEmpty()) {
                        Proveedor nuevo = new Proveedor();
                        copiarProveedor(request, nuevo);
                        nuevo.setNegocioIdnegocio(SesionActual.negocioActivo(request));
                        proveedorF.create(nuevo);
                        response.sendRedirect(request.getContextPath() + "/ListarProveedores");
                        return;
                    }
                    request.setAttribute("errores", errores);
                }
                url = "/WEB-INF/views/FormProveedor.jsp";
                break;

            case "/EditarProveedor": {
                Integer id = entero(request, "id");
                Proveedor proveedor = id == null ? null : proveedorF.findEnNegocio(id, idNegocio);
                if (proveedor == null) {
                    response.sendRedirect(request.getContextPath() + "/ListarProveedores?error="
                            + codificar("Ese proveedor ya no existe."));
                    return;
                }
                if (post) {
                    List<String> errores = validarProveedor(request, id, idNegocio);
                    if (errores.isEmpty()) {
                        copiarProveedor(request, proveedor);
                        proveedorF.edit(proveedor);
                        response.sendRedirect(request.getContextPath() + "/ListarProveedores");
                        return;
                    }
                    request.setAttribute("errores", errores);
                }
                request.setAttribute("proveedor", proveedor);
                url = "/WEB-INF/views/FormProveedor.jsp";
                break;
            }

            case "/EliminarProveedor": {
                String error = "";
                Integer id = entero(request, "id");
                if (post && id != null) {
                    Proveedor proveedor = proveedorF.findEnNegocio(id, idNegocio);
                    if (proveedor == null) {
                        error = "?error=" + codificar("Ese proveedor ya no existe.");
                    } else if (proveedorF.hasRelations(id)) {
                        error = "?error=" + codificar("No se puede eliminar el proveedor: "
                                + "tiene productos asignados o compras registradas.");
                    } else {
                        proveedorF.remove(proveedor);
                    }
                }
                response.sendRedirect(request.getContextPath() + "/ListarProveedores" + error);
                return;
            }

            default:
                break;
        }
        despachar(request, response, url);
    }

    private List<String> validarProveedor(HttpServletRequest request, Integer idActual,
            Integer idNegocio) {
        List<String> errores = new ArrayList<>();

        String nombre = parametroTexto(request, "nombre");
        if (nombre == null) {
            errores.add("El nombre es obligatorio.");
        } else if (nombre.length() > LARGO_NOMBRE) {
            errores.add("El nombre no puede tener más de " + LARGO_NOMBRE + " caracteres.");
        } else if (proveedorF.existeNombre(nombre, idActual, idNegocio)) {
            errores.add("Ya existe un proveedor llamado " + nombre + ".");
        }

        String telefono = parametroTexto(request, "telefono");
        if (telefono != null && telefono.length() > LARGO_TELEFONO) {
            errores.add("El teléfono no puede tener más de " + LARGO_TELEFONO + " caracteres.");
        }

        String email = parametroTexto(request, "email");
        if (email != null) {
            if (email.length() > LARGO_EMAIL) {
                errores.add("El email no puede tener más de " + LARGO_EMAIL + " caracteres.");
            } else if (!email.matches("[^@\\s]+@[^@\\s.]+\\.[^@\\s]+")) {
                errores.add("El email no tiene un formato válido.");
            }
        }
        return errores;
    }

    private void copiarProveedor(HttpServletRequest request, Proveedor proveedor) {
        proveedor.setNombre(parametroTexto(request, "nombre"));
        proveedor.setTelefono(parametroTexto(request, "telefono"));
        proveedor.setEmail(parametroTexto(request, "email"));
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
