package clinica.acceso;

import clinica.database.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccesoHibernateIntegrationTest {

    @AfterAll
    static void cerrarFactory() {
        JPAUtil.cerrar();
    }

    @Test
    void autenticaYValidaRolesContraPostgresqlConHibernate() {
        Assumptions.assumeTrue(
                System.getenv("CLINICA_DB_URL") != null
                        && System.getenv("CLINICA_DB_USER") != null
                        && System.getenv("CLINICA_DB_PASSWORD") != null,
                "Variables CLINICA_DB_* no configuradas; se omite prueba de integración.");

        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        String userRecep = "it_recep_" + sufijo;
        String userMed = "it_med_" + sufijo;

        UsuarioDAO dao = new UsuarioDAO();
        AccesoService service = new AccesoService(dao::buscarPorUsuario);

        try {
            dao.guardar(new Usuario(userRecep, SeguridadClave.hash("TmpPass1!"), "Prueba Recepción", Rol.RECEPCION));
            dao.guardar(new Usuario(userMed, SeguridadClave.hash("TmpPass2!"), "Prueba Médico", Rol.MEDICO));

            Optional<Usuario> recep = service.autenticar(userRecep, "TmpPass1!");
            assertTrue(recep.isPresent());
            assertEquals(Rol.RECEPCION, recep.get().getRol());
            assertThrows(SecurityException.class, () -> service.exigirPermiso(recep.get(), Modulo.EXPEDIENTE));

            Optional<Usuario> med = service.autenticar(userMed, "TmpPass2!");
            assertTrue(med.isPresent());
            assertEquals(Rol.MEDICO, med.get().getRol());
            assertThrows(SecurityException.class, () -> service.exigirPermiso(med.get(), Modulo.PACIENTES));
            assertThrows(SecurityException.class, () -> service.exigirPermiso(med.get(), Modulo.COBROS));

            assertTrue(service.autenticar(userRecep, "ClaveIncorrecta").isEmpty());
        } finally {
            eliminarUsuarioSiExiste(userRecep);
            eliminarUsuarioSiExiste(userMed);
        }
    }

    private void eliminarUsuarioSiExiste(String usuario) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            tx.begin();
            em.createQuery("DELETE FROM Usuario u WHERE u.usuario = :usuario")
                    .setParameter("usuario", usuario)
                    .executeUpdate();
            tx.commit();
        }
    }
}
