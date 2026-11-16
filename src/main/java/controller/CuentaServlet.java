package controller;

import entities.Membresia;
import entities.MembresiaPK;
import entities.Negocio;
import entities.Usuario;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import sessions.Hash;
import sessions.MembresiaFacade;
import sessions.NegocioFacade;
import sessions.SesionActual;
import sessions.UsuarioFacade;

@WebServlet(name = "CuentaServlet", urlPatterns = {
    "/IniciarSesion", "/Registrarse", "/CerrarSesion",
    "/Negocios", "/CrearNegocio",
    "/Empleados", "/AgregarEmpleado", "/CambiarRolEmpleado", "/EliminarEmpleado"})
public class CuentaServlet extends BaseControlador {

    private static final int LARGO_EMAIL = 60;
    private static final int LARGO_NOMBRE_USUARIO = 60;
    private static final int LARGO_NOMBRE_NEGOCIO = 60;
    private static final int LARGO_PASSWORD_MINIMO = 6;

    @EJB
    private UsuarioFacade usuarioF;
    @EJB
    private NegocioFacade negocioF;
    @EJB
    private MembresiaFacade membresiaF;

    private void procesar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();
        String url = "/WEB-INF/views/IniciarSesion.jsp";
        boolean post = "POST".equals(request.getMethod());
        Integer idNegocio = SesionActual.idNegocioActivo(request);

        switch (path) {
            case "/IniciarSesion":
                if (post) {
                    Usuario usuario = usuarioF.validarCredenciales(
                            request.getParameter("email"), request.getParameter("password"));
                    if (usuario == null) {
                        request.setAttribute("errores",
                                List.of("El email o la contraseña no son correctos."));
                    } else {
                        SesionActual.iniciar(request, usuario);
                        List<Membresia> membresias = membresiaF.findByUsuario(usuario.getIdusuario());
                        if (membresias.size() == 1) {
                            Membresia unica = membresias.get(0);
                            SesionActual.elegirNegocio(request, unica.getNegocio(), unica.getRol());
                            response.sendRedirect(request.getContextPath() + "/Panel");
                        } else {
                            response.sendRedirect(request.getContextPath() + "/Negocios");
                        }
                        return;
                    }
                }
                url = "/WEB-INF/views/IniciarSesion.jsp";
                break;

            case "/Registrarse":
                if (post) {
                    List<String> errores = validarRegistro(request);
                    if (errores.isEmpty()) {
                        Usuario usuario = new Usuario();
                        usuario.setEmail(parametroTexto(request, "email").toLowerCase());
                        usuario.setPasswordHash(Hash.sha256(request.getParameter("password")));
                        usuario.setNombre(parametroTexto(request, "nombre"));

                        Negocio negocio = new Negocio();
                        negocio.setNombre(parametroTexto(request, "nombreNegocio"));
                        negocio.setFechaCreacion(new Date());

                        usuarioF.registrar(usuario, negocio);

                        SesionActual.iniciar(request, usuario);
                        SesionActual.elegirNegocio(request, negocio, MembresiaFacade.PROPIETARIO);
                        response.sendRedirect(request.getContextPath() + "/Panel");
                        return;
                    }
                    request.setAttribute("errores", errores);
                }
                url = "/WEB-INF/views/Registrarse.jsp";
                break;

            case "/CerrarSesion":
                SesionActual.cerrar(request);
                response.sendRedirect(request.getContextPath() + "/IniciarSesion");
                return;

            case "/Negocios": {
                Usuario usuario = SesionActual.usuario(request);
                if (post && entero(request, "idNegocio") != null) {
                    Integer idElegido = entero(request, "idNegocio");
                    Membresia membresia = membresiaF.findRol(usuario.getIdusuario(), idElegido);
                    if (membresia != null) {
                        SesionActual.elegirNegocio(request, membresia.getNegocio(), membresia.getRol());
                        response.sendRedirect(request.getContextPath() + "/Panel");
                        return;
                    }
                }
                request.setAttribute("membresias", membresiaF.findByUsuario(usuario.getIdusuario()));
                url = "/WEB-INF/views/Negocios.jsp";
                break;
            }

            case "/CrearNegocio": {
                if (post) {
                    Usuario usuario = SesionActual.usuario(request);
                    String nombre = parametroTexto(request, "nombre");
                    if (nombre == null || nombre.length() > LARGO_NOMBRE_NEGOCIO) {
                        response.sendRedirect(request.getContextPath() + "/Negocios?error="
                                + codificar("El nombre del negocio es obligatorio y no puede "
                                        + "superar los " + LARGO_NOMBRE_NEGOCIO + " caracteres."));
                        return;
                    }
                    Negocio negocio = new Negocio();
                    negocio.setNombre(nombre);
                    negocio.setFechaCreacion(new Date());
                    negocioF.crearConPropietario(negocio, usuario.getIdusuario());
                    SesionActual.elegirNegocio(request, negocio, MembresiaFacade.PROPIETARIO);
                }
                response.sendRedirect(request.getContextPath() + "/Panel");
                return;
            }

            case "/Empleados":
                request.setAttribute("membresias", membresiaF.findByNegocio(idNegocio));
                request.setAttribute("usuarioActual", SesionActual.usuario(request));
                url = "/WEB-INF/views/Empleados.jsp";
                break;

            case "/AgregarEmpleado": {
                String error = agregarEmpleado(request, idNegocio);
                response.sendRedirect(request.getContextPath() + "/Empleados"
                        + (error == null ? "" : "?error=" + codificar(error)));
                return;
            }

            case "/CambiarRolEmpleado": {
                String error = cambiarRolEmpleado(request, idNegocio);
                response.sendRedirect(request.getContextPath() + "/Empleados"
                        + (error == null ? "" : "?error=" + codificar(error)));
                return;
            }

            case "/EliminarEmpleado": {
                String error = eliminarEmpleado(request, idNegocio);
                response.sendRedirect(request.getContextPath() + "/Empleados"
                        + (error == null ? "" : "?error=" + codificar(error)));
                return;
            }

            default:
                break;
        }
        despachar(request, response, url);
    }

    private String agregarEmpleado(HttpServletRequest request, Integer idNegocio) {
        if (!"POST".equals(request.getMethod())) {
            return null;
        }
        String email = parametroTexto(request, "email");
        String rol = request.getParameter("rol");
        if (email == null) {
            return "El email es obligatorio.";
        }
        if (!MembresiaFacade.PROPIETARIO.equals(rol) && !MembresiaFacade.EMPLEADO.equals(rol)) {
            return "Hay que elegir un rol válido.";
        }
        Usuario usuario = usuarioF.findByEmail(email);
        if (usuario == null) {
            return "No hay ningún usuario registrado con ese email. Tiene que registrarse primero.";
        }
        if (membresiaF.findRol(usuario.getIdusuario(), idNegocio) != null) {
            return "Ese usuario ya pertenece a este negocio.";
        }
        Membresia membresia = new Membresia(new MembresiaPK(usuario.getIdusuario(), idNegocio));
        membresia.setRol(rol);
        membresiaF.create(membresia);
        return null;
    }

    private String cambiarRolEmpleado(HttpServletRequest request, Integer idNegocio) {
        if (!"POST".equals(request.getMethod())) {
            return null;
        }
        Integer idUsuario = entero(request, "idUsuario");
        String nuevoRol = request.getParameter("rol");
        if (idUsuario == null
                || (!MembresiaFacade.PROPIETARIO.equals(nuevoRol)
                && !MembresiaFacade.EMPLEADO.equals(nuevoRol))) {
            return "Datos inválidos.";
        }
        Membresia membresia = membresiaF.findRol(idUsuario, idNegocio);
        if (membresia == null) {
            return "Ese usuario no pertenece a este negocio.";
        }
        if (MembresiaFacade.PROPIETARIO.equals(membresia.getRol())
                && !MembresiaFacade.PROPIETARIO.equals(nuevoRol)
                && membresiaF.countPropietarios(idNegocio) <= 1) {
            return "Tiene que quedar al menos un propietario en el negocio.";
        }
        membresia.setRol(nuevoRol);
        membresiaF.edit(membresia);
        Usuario actual = SesionActual.usuario(request);
        if (actual != null && actual.getIdusuario().equals(idUsuario)) {
            SesionActual.elegirNegocio(request, SesionActual.negocioActivo(request), nuevoRol);
        }
        return null;
    }

    private String eliminarEmpleado(HttpServletRequest request, Integer idNegocio) {
        if (!"POST".equals(request.getMethod())) {
            return null;
        }
        Integer idUsuario = entero(request, "idUsuario");
        if (idUsuario == null) {
            return "Datos inválidos.";
        }
        Membresia membresia = membresiaF.findRol(idUsuario, idNegocio);
        if (membresia == null) {
            return "Ese usuario no pertenece a este negocio.";
        }
        if (MembresiaFacade.PROPIETARIO.equals(membresia.getRol())
                && membresiaF.countPropietarios(idNegocio) <= 1) {
            return "Tiene que quedar al menos un propietario en el negocio.";
        }
        membresiaF.remove(membresia);
        return null;
    }

    private List<String> validarRegistro(HttpServletRequest request) {
        List<String> errores = new ArrayList<>();

        String email = parametroTexto(request, "email");
        if (email == null || !email.matches("[^@\\s]+@[^@\\s.]+\\.[^@\\s]+")) {
            errores.add("El email no tiene un formato válido.");
        } else if (email.length() > LARGO_EMAIL) {
            errores.add("El email no puede tener más de " + LARGO_EMAIL + " caracteres.");
        } else if (usuarioF.existeEmail(email)) {
            errores.add("Ya hay una cuenta registrada con ese email.");
        }

        String password = request.getParameter("password");
        if (password == null || password.length() < LARGO_PASSWORD_MINIMO) {
            errores.add("La contraseña tiene que tener al menos " + LARGO_PASSWORD_MINIMO
                    + " caracteres.");
        }

        String nombre = parametroTexto(request, "nombre");
        if (nombre == null) {
            errores.add("El nombre es obligatorio.");
        } else if (nombre.length() > LARGO_NOMBRE_USUARIO) {
            errores.add("El nombre no puede tener más de " + LARGO_NOMBRE_USUARIO + " caracteres.");
        }

        String nombreNegocio = parametroTexto(request, "nombreNegocio");
        if (nombreNegocio == null) {
            errores.add("El nombre del negocio es obligatorio.");
        } else if (nombreNegocio.length() > LARGO_NOMBRE_NEGOCIO) {
            errores.add("El nombre del negocio no puede tener más de " + LARGO_NOMBRE_NEGOCIO
                    + " caracteres.");
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
