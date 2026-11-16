package sessions;

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
        List<Usuario> lista = getEntityManager().createQuery(
                "SELECT u FROM Usuario u WHERE LOWER(u.email) = :email", Usuario.class)
                .setParameter("email", email.trim().toLowerCase()).setMaxResults(1).getResultList();
        return lista.isEmpty() ? null : lista.get(0);
    }

    public boolean existeEmail(String email) {
        return findByEmail(email) != null;
    }

    public Usuario validarCredenciales(String email, String password) {
        Usuario usuario = findByEmail(email);
        if (usuario == null) {
            return null;
        }
        return usuario.getPasswordHash().equals(Hash.sha256(password)) ? usuario : null;
    }

}
