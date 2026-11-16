package sessions;

import entities.Membresia;
import entities.MembresiaPK;
import entities.Negocio;
import entities.Usuario;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class UsuarioFacade extends AbstractFacade<Usuario> {

    @PersistenceContext(unitName = "AppInventarioPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsuarioFacade() {
        super(Usuario.class);
    }

    public Usuario findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        List<Usuario> lista = getEntityManager().createQuery(
                "SELECT u FROM Usuario u WHERE LOWER(u.email) = :email", Usuario.class)
                .setParameter("email", email.trim().toLowerCase()).setMaxResults(1).getResultList();
        return lista.isEmpty() ? null : lista.get(0);
    }

    public boolean existeEmail(String email) {
        return findByEmail(email) != null;
    }

    public Usuario validarCredenciales(String email, String password) {
        if (password == null || password.isEmpty()) {
            return null;
        }
        Usuario usuario = findByEmail(email);
        if (usuario == null) {
            return null;
        }
        return usuario.getPasswordHash().equals(Hash.sha256(password)) ? usuario : null;
    }

    public Usuario registrar(Usuario usuario, Negocio negocio) {
        em.persist(usuario);
        em.persist(negocio);
        em.flush();
        Membresia membresia = new Membresia(
                new MembresiaPK(usuario.getIdusuario(), negocio.getIdnegocio()));
        membresia.setRol(MembresiaFacade.PROPIETARIO);
        em.persist(membresia);
        return usuario;
    }

}
