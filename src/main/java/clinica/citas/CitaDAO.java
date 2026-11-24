package clinica.citas;

import clinica.acceso.Rol;
import clinica.acceso.Usuario;
import clinica.database.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Acceso a datos de Cita, Consultorio y médicos disponibles mediante Hibernate/JPA.
 */
public class CitaDAO {

    public List<Usuario> listarMedicos() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery(
                            "SELECT u FROM Usuario u WHERE u.rol = :rol ORDER BY u.nombre", Usuario.class)
                    .setParameter("rol", Rol.MEDICO)
                    .getResultList();
        }
    }

    public List<Consultorio> listarConsultorios() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery(
                            "SELECT c FROM Consultorio c ORDER BY c.nombre", Consultorio.class)
                    .getResultList();
        }
    }

    public List<Cita> listarPorFecha(LocalDate fecha) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery(
                            "SELECT c FROM Cita c "
                                    + "JOIN FETCH c.paciente "
                                    + "JOIN FETCH c.medico "
                                    + "JOIN FETCH c.consultorio "
                                    + "WHERE c.fecha = :fecha "
                                    + "ORDER BY c.hora",
                            Cita.class)
                    .setParameter("fecha", fecha)
                    .getResultList();
        }
    }

    public List<Cita> listarPorFechaYMedico(LocalDate fecha, Integer medicoId) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery(
                            "SELECT c FROM Cita c "
                                    + "JOIN FETCH c.paciente "
                                    + "JOIN FETCH c.medico "
                                    + "JOIN FETCH c.consultorio "
                                    + "WHERE c.fecha = :fecha AND c.medico.id = :medicoId "
                                    + "ORDER BY c.hora",
                            Cita.class)
                    .setParameter("fecha", fecha)
                    .setParameter("medicoId", medicoId)
                    .getResultList();
        }
    }

    public boolean existeChoqueMedico(Integer medicoId, LocalDate fecha, LocalTime hora) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            Long count = em.createQuery(
                            "SELECT COUNT(c) FROM Cita c "
                                    + "WHERE c.medico.id = :medicoId AND c.fecha = :fecha AND c.hora = :hora",
                            Long.class)
                    .setParameter("medicoId", medicoId)
                    .setParameter("fecha", fecha)
                    .setParameter("hora", hora)
                    .getSingleResult();
            return count != null && count > 0;
        }
    }

    public boolean existeChoqueConsultorio(Integer consultorioId, LocalDate fecha, LocalTime hora) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            Long count = em.createQuery(
                            "SELECT COUNT(c) FROM Cita c "
                                    + "WHERE c.consultorio.id = :consultorioId AND c.fecha = :fecha AND c.hora = :hora",
                            Long.class)
                    .setParameter("consultorioId", consultorioId)
                    .setParameter("fecha", fecha)
                    .setParameter("hora", hora)
                    .getSingleResult();
            return count != null && count > 0;
        }
    }

    public Cita guardar(Cita cita) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(cita);
            tx.commit();
            return cita;
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
