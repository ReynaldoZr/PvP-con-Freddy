package clinica.pacientes;

import clinica.database.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

/**
 * Acceso a datos de Paciente mediante Hibernate/JPA con consultas parametrizadas.
 */
public class PacienteDAO {

    public List<Paciente> listarTodos() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery(
                            "SELECT p FROM Paciente p ORDER BY p.apellidos, p.nombres", Paciente.class)
                    .getResultList();
        }
    }

    public List<Paciente> buscarPorNombreOApellido(String filtro) {
        if (filtro == null || filtro.isBlank()) {
            return listarTodos();
        }
        String patron = "%" + filtro.trim().toLowerCase() + "%";
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery(
                            "SELECT p FROM Paciente p "
                                    + "WHERE LOWER(p.nombres) LIKE :patron "
                                    + "OR LOWER(p.apellidos) LIKE :patron "
                                    + "OR LOWER(CONCAT(p.nombres, ' ', p.apellidos)) LIKE :patron "
                                    + "ORDER BY p.apellidos, p.nombres",
                            Paciente.class)
                    .setParameter("patron", patron)
                    .getResultList();
        }
    }

    public Paciente guardar(Paciente paciente) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(paciente);
            tx.commit();
            return paciente;
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public Paciente actualizar(Paciente paciente) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Paciente actualizado = em.merge(paciente);
            tx.commit();
            return actualizado;
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
