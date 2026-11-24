package clinica.pacientes;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PacienteServiceTest {

    private final PacienteService service = new PacienteService(new PacienteDAO());

    @Test
    void validaCamposObligatoriosYFechaNoFutura() {
        assertThrows(IllegalArgumentException.class,
                () -> service.validarYConstruir(null, " ", "Pérez", LocalDate.of(1995, 5, 10), "88881111"));
        assertThrows(IllegalArgumentException.class,
                () -> service.validarYConstruir(null, "María", "", LocalDate.of(1995, 5, 10), "88881111"));
        assertThrows(IllegalArgumentException.class,
                () -> service.validarYConstruir(null, "María", "Pérez", LocalDate.now().plusDays(1), "88881111"));
    }

    @Test
    void normalizaEspaciosYTelefonoOpcional() {
        Paciente p = service.validarYConstruir(null, "  María ", "  Pérez  ", LocalDate.of(1995, 5, 10), "   ");
        assertEquals("María", p.getNombres());
        assertEquals("Pérez", p.getApellidos());
        assertNull(p.getTelefono());
    }
}
