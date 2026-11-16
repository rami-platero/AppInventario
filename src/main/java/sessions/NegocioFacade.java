package sessions;

import entities.Membresia;
import entities.MembresiaPK;
import entities.Negocio;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class NegocioFacade extends AbstractFacade<Negocio> {

    @PersistenceContext(unitName = "AppInventarioPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public NegocioFacade() {
        super(Negocio.class);
    }

    public Negocio crearConPropietario(Negocio negocio, Integer idUsuario) {
        em.persist(negocio);
        em.flush();
        Membresia membresia = new Membresia(
                new MembresiaPK(idUsuario, negocio.getIdnegocio()));
        membresia.setRol(MembresiaFacade.PROPIETARIO);
        em.persist(membresia);
        return negocio;
    }

}
