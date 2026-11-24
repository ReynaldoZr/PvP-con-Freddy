package clinica.acceso;

import clinica.database.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Optional;

/**
 * Acceso a datos de la entidad Usuario mediante Hibernate/JPA.
 */
public class UsuarioDAO {

    public Optional<Usuario> buscarPorUsuario(String usuario) {
        if (usuario == null || usuario.isBlank()) {
            return Optional.empty();
        }
        try (EntityManager em = JPAUtil.getEntityManager()) {
            List<Usuario> resultados = em.createQuery(
                            "SELECT u FROM Usuario u WHERE u.usuario = :usuario", Usuario.class)
                    .setParameter("usuario", usuario.trim())
                    .setMaxResults(1)
                    .getResultList();
            return resultados.stream().findFirst();
        }
    }

    public void guardar(Usuario usuario) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(usuario);
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }
}
