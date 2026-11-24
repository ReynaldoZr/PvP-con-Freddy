package clinica.expediente;

import clinica.database.JPAUtil;
import clinica.citas.Cita;
import clinica.acceso.Rol;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDateTime;
import java.util.List;

/** Reglas de negocio del expediente mediante la conexión JPA compartida. */
public class ExpedienteService {

    /** Crea la atención de la cita o actualiza la existente. */
    public void guardarAtencion(int citaId, String motivo, String diagnostico,
                                String indicaciones, int usuarioId) {
        if (citaId <= 0 || usuarioId <= 0) {
            throw new IllegalArgumentException("La cita y el usuario deben ser válidos.");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("El motivo es obligatorio.");
        }
        if (diagnostico == null || diagnostico.isBlank()) {
            throw new IllegalArgumentException("El diagnóstico es obligatorio.");
        }

        try (EntityManager session = JPAUtil.getEntityManager()) {
            EntityTransaction transaction = session.getTransaction();
            transaction.begin();
            try {
                exigirCitaDelMedico(session, citaId, usuarioId);
                Consulta consulta = session.createQuery(
                                "from Consulta c where c.citaId = :citaId", Consulta.class)
                        .setParameter("citaId", citaId)
                        .getSingleResultOrNull();

                if (consulta == null) {
                    consulta = new Consulta();
                    consulta.setCitaId(citaId);
                }

                consulta.setMotivo(motivo.trim());
                consulta.setDiagnostico(diagnostico.trim());
                consulta.setIndicaciones(indicaciones == null ? "" : indicaciones.trim());
                consulta.setActualizadoPorId(usuarioId);
                consulta.setActualizadoEn(LocalDateTime.now());
                session.merge(consulta);
                transaction.commit();
            } catch (RuntimeException ex) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                throw ex;
            }
        }
    }

    /** Recupera la atención actual de una cita para permitir editarla. */
    public Consulta obtenerAtencionPorCita(int citaId) {
        try (EntityManager session = JPAUtil.getEntityManager()) {
            return session.createQuery(
                            "from Consulta c where c.citaId = :citaId", Consulta.class)
                    .setParameter("citaId", citaId)
                    .getSingleResultOrNull();
        }
    }

    /** Verifica que el médico autenticado sea el asignado a la cita. */
    public void verificarCita(int citaId, int usuarioId) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            exigirCitaDelMedico(em, citaId, usuarioId);
        }
    }

    private void exigirCitaDelMedico(EntityManager em, int citaId, int usuarioId) {
        Cita cita = em.find(Cita.class, citaId);
        if (cita == null || cita.getMedico().getId() != usuarioId
                || cita.getMedico().getRol() != Rol.MEDICO) {
            throw new SecurityException("La cita no pertenece al médico autenticado.");
        }
    }

    /** Historial de todas las atenciones del paciente de la cita. */
    public List<AtencionHistorial> listarHistorialPorCita(int citaId) {
        String jpql = """
                select new clinica.expediente.AtencionHistorial(
                    ci.fecha, ci.hora, c.motivo, c.diagnostico,
                    c.indicaciones, u.nombre, c.actualizadoEn)
                from Consulta c, Cita ci, Usuario u
                where ci.id = c.citaId and u.id = c.actualizadoPorId
                  and ci.paciente.id = (select origen.paciente.id from Cita origen where origen.id = :citaId)
                order by ci.fecha desc, ci.hora desc, c.id desc
                """;
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery(jpql, AtencionHistorial.class)
                    .setParameter("citaId", citaId).getResultList();
        }
    }
}
