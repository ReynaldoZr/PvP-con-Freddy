package clinica.acceso;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccesoServiceTest {

    @Test
    void autenticaRecepcionYMedicoYRechazaClaveIncorrecta() {
        Usuario recepcion = new Usuario("recep.ana", SeguridadClave.hash("ClavePrueba1!"), "Ana López", Rol.RECEPCION);
        Usuario medico = new Usuario("med.carlos", SeguridadClave.hash("ClavePrueba2!"), "Dr. Carlos Ruiz", Rol.MEDICO);

        Map<String, Usuario> usuarios = Map.of(
                recepcion.getUsuario(), recepcion,
                medico.getUsuario(), medico
        );
        AccesoService service = new AccesoService(nombre -> Optional.ofNullable(usuarios.get(nombre)));

        Optional<Usuario> loginRecepcion = service.autenticar("recep.ana", "ClavePrueba1!");
        assertTrue(loginRecepcion.isPresent());
        assertEquals(Rol.RECEPCION, loginRecepcion.get().getRol());

        Optional<Usuario> loginMedico = service.autenticar("med.carlos", "ClavePrueba2!");
        assertTrue(loginMedico.isPresent());
        assertEquals(Rol.MEDICO, loginMedico.get().getRol());

        assertTrue(service.autenticar("recep.ana", "ClaveIncorrecta").isEmpty());
        assertTrue(service.autenticar("no.existe", "ClavePrueba1!").isEmpty());
        assertTrue(service.autenticar(" ", "ClavePrueba1!").isEmpty());
    }

    @Test
    void recepcionSoloAbrePacientesCitasYCobros() {
        Usuario recepcion = new Usuario("recep.ana", "hash", "Ana López", Rol.RECEPCION);
        AccesoService service = new AccesoService(u -> Optional.empty());

        assertDoesNotThrow(() -> service.exigirPermiso(recepcion, Modulo.PACIENTES));
        assertDoesNotThrow(() -> service.exigirPermiso(recepcion, Modulo.CITAS));
        assertDoesNotThrow(() -> service.exigirPermiso(recepcion, Modulo.COBROS));
        assertThrows(SecurityException.class, () -> service.exigirPermiso(recepcion, Modulo.EXPEDIENTE));
    }

    @Test
    void medicoSoloAbreCitasYExpediente() {
        Usuario medico = new Usuario("med.carlos", "hash", "Dr. Carlos Ruiz", Rol.MEDICO);
        AccesoService service = new AccesoService(u -> Optional.empty());

        assertDoesNotThrow(() -> service.exigirPermiso(medico, Modulo.CITAS));
        assertDoesNotThrow(() -> service.exigirPermiso(medico, Modulo.EXPEDIENTE));
        assertThrows(SecurityException.class, () -> service.exigirPermiso(medico, Modulo.PACIENTES));
        assertThrows(SecurityException.class, () -> service.exigirPermiso(medico, Modulo.COBROS));
    }

    @Test
    void seguridadClaveRechazaHashesInvalidos() {
        assertFalse(SeguridadClave.verificar("algo", "formato-invalido"));
        assertFalse(SeguridadClave.verificar("", SeguridadClave.hash("secreto")));
    }
}
