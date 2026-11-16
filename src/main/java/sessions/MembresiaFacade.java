package sessions;

import entities.Membresia;
import entities.MembresiaPK;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class MembresiaFacade extends AbstractFacade<Membresia> {

    public static final String PROPIETARIO = "PROPIETARIO";
    public static final String EMPLEADO = "EMPLEADO";

    @PersistenceContext(unitName = "AppInventarioPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MembresiaFacade() {
        super(Membresia.class);
    }

    public List<Membresia> findByUsuario(Integer idUsuario) {
        return getEntityManager().createQuery(
                "SELECT m FROM Membresia m WHERE m.membresiaPK.usuarioIdusuario = :id "
                + "ORDER BY m.negocio.nombre", Membresia.class)
                .setParameter("id", idUsuario).getResultList();
    }

    public List<Membresia> findByNegocio(Integer idNegocio) {
        return getEntityManager().createQuery(
                "SELECT m FROM Membresia m WHERE m.membresiaPK.negocioIdnegocio = :id "
                + "ORDER BY m.usuario.nombre", Membresia.class)
                .setParameter("id", idNegocio).getResultList();
    }

    public Membresia findRol(Integer idUsuario, Integer idNegocio) {
        return find(new MembresiaPK(idUsuario, idNegocio));
    }

    public boolean esPropietario(Integer idUsuario, Integer idNegocio) {
        Membresia membresia = findRol(idUsuario, idNegocio);
        return membresia != null && PROPIETARIO.equals(membresia.getRol());
    }

    public long countPropietarios(Integer idNegocio) {
        return getEntityManager().createQuery(
                "SELECT COUNT(m) FROM Membresia m WHERE m.membresiaPK.negocioIdnegocio = :id "
                + "AND m.rol = :rol", Long.class)
                .setParameter("id", idNegocio).setParameter("rol", PROPIETARIO).getSingleResult();
    }

}
