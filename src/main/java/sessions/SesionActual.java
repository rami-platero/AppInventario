package sessions;

import entities.Negocio;
import entities.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public final class SesionActual {

    public static final String USUARIO = "usuario";
    public static final String NEGOCIO_ACTIVO = "negocioActivo";
    public static final String ROL_ACTIVO = "rolActivo";

    private SesionActual() {
    }

    public static Usuario usuario(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        return sesion == null ? null : (Usuario) sesion.getAttribute(USUARIO);
    }

    public static Negocio negocioActivo(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        return sesion == null ? null : (Negocio) sesion.getAttribute(NEGOCIO_ACTIVO);
    }

    public static Integer idNegocioActivo(HttpServletRequest request) {
        Negocio negocio = negocioActivo(request);
        return negocio == null ? null : negocio.getIdnegocio();
    }

    public static String rolActivo(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        return sesion == null ? null : (String) sesion.getAttribute(ROL_ACTIVO);
    }

    public static boolean esPropietario(HttpServletRequest request) {
        return MembresiaFacade.PROPIETARIO.equals(rolActivo(request));
    }

    public static void iniciar(HttpServletRequest request, Usuario usuario) {
        HttpSession sesion = request.getSession(true);
        sesion.setAttribute(USUARIO, usuario);
        sesion.removeAttribute(NEGOCIO_ACTIVO);
        sesion.removeAttribute(ROL_ACTIVO);
    }

    public static void elegirNegocio(HttpServletRequest request, Negocio negocio, String rol) {
        HttpSession sesion = request.getSession(true);
        sesion.setAttribute(NEGOCIO_ACTIVO, negocio);
        sesion.setAttribute(ROL_ACTIVO, rol);
    }

    public static void cerrar(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        if (sesion != null) {
            sesion.invalidate();
        }
    }

}
