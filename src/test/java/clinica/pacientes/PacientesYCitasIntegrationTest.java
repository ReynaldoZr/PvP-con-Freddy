package clinica.pacientes;

import clinica.acceso.Rol;
import clinica.acceso.SeguridadClave;
import clinica.acceso.Usuario;
import clinica.acceso.UsuarioDAO;
import clinica.citas.Cita;
import clinica.citas.CitaDAO;
import clinica.citas.CitaService;
import clinica.citas.Consultorio;
import clinica.database.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PacientesYCitasIntegrationTest {

    @AfterAll
    static void cerrarFactory() {
        JPAUtil.cerrar();
    }

    @Test
    void registraBuscaPacienteYRechazaChoquesDeMedicoYConsultorio() {
        Assumptions.assumeTrue(
                System.getenv("CLINICA_DB_URL") != null
                        && System.getenv("CLINICA_DB_USER") != null
                        && System.getenv("CLINICA_DB_PASSWORD") != null,
                "Variables CLINICA_DB_* no configuradas; se omite prueba de integración.");

        String sufijo = UUID.randomUUID().toString().substring(0, 6);
        PacienteService pacienteService = new PacienteService();
        CitaService citaService = new CitaService();
        UsuarioDAO usuarioDAO = new UsuarioDAO();

        Paciente p1 = null;
        Paciente p2 = null;
        Usuario med1 = new Usuario("it_med1_" + sufijo, SeguridadClave.hash("Tmp1!"), "Dr. Uno " + sufijo, Rol.MEDICO);
        Usuario med2 = new Usuario("it_med2_" + sufijo, SeguridadClave.hash("Tmp2!"), "Dr. Dos " + sufijo, Rol.MEDICO);
        Cita citaBase = null;

        try {
            usuarioDAO.guardar(med1);
            usuarioDAO.guardar(med2);

            p1 = pacienteService.registrar("Prueba" + sufijo, "Apellido" + sufijo,
                    LocalDate.of(1998, 4, 15), "88880001");
            p2 = pacienteService.registrar("Otro" + sufijo, "Paciente" + sufijo,
                    LocalDate.of(1999, 8, 20), "88880002");

            assertNotNull(p1.getId());
            pacienteService.actualizar(p1, "PruebaEdit" + sufijo, "Apellido" + sufijo,
                    LocalDate.of(1998, 4, 15), "88889999");

            List<Paciente> encontrados = pacienteService.buscar("PruebaEdit" + sufijo);
            assertEquals(1, encontrados.size());
            assertEquals("88889999", encontrados.getFirst().getTelefono());

            List<Consultorio> consultorios = citaService.listarConsultorios();
            assertTrue(consultorios.size() >= 2, "Debe haber al menos 2 consultorios de prueba.");
            Consultorio c1 = consultorios.get(0);
            Consultorio c2 = consultorios.get(1);

            LocalDate fecha = LocalDate.now().plusDays(10);
            LocalTime hora = LocalTime.of(10, 30);

            citaBase = citaService.agendar(p1, med1, c1, fecha, hora);
            assertNotNull(citaBase.getId());

            final Paciente segundoPaciente = p2;

            // Choque de médico (mismo médico, otro consultorio)
            IllegalStateException choqueMedico = assertThrows(IllegalStateException.class,
                    () -> citaService.agendar(segundoPaciente, med1, c2, fecha, hora));
            assertTrue(choqueMedico.getMessage().contains("médico"));

            // Choque de consultorio (otro médico, mismo consultorio)
            IllegalStateException choqueConsultorio = assertThrows(IllegalStateException.class,
                    () -> citaService.agendar(segundoPaciente, med2, c1, fecha, hora));
            assertTrue(choqueConsultorio.getMessage().contains("consultorio"));
        } finally {
            limpiarDatosPrueba(citaBase, p1, p2, med1, med2);
        }
    }

    private void limpiarDatosPrueba(Cita cita, Paciente p1, Paciente p2, Usuario m1, Usuario m2) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            tx.begin();
            if (cita != null && cita.getId() != null) {
                em.createQuery("DELETE FROM Cita c WHERE c.id = :id").setParameter("id", cita.getId()).executeUpdate();
            }
            if (p1 != null && p1.getId() != null) {
                em.createQuery("DELETE FROM Paciente p WHERE p.id = :id").setParameter("id", p1.getId()).executeUpdate();
            }
            if (p2 != null && p2.getId() != null) {
                em.createQuery("DELETE FROM Paciente p WHERE p.id = :id").setParameter("id", p2.getId()).executeUpdate();
            }
            if (m1 != null && m1.getId() != null) {
                em.createQuery("DELETE FROM Usuario u WHERE u.id = :id").setParameter("id", m1.getId()).executeUpdate();
            }
            if (m2 != null && m2.getId() != null) {
                em.createQuery("DELETE FROM Usuario u WHERE u.id = :id").setParameter("id", m2.getId()).executeUpdate();
            }
            tx.commit();
        }
    }
}
