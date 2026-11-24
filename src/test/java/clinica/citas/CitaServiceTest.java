package clinica.citas;

import clinica.acceso.Rol;
import clinica.acceso.Usuario;
import clinica.pacientes.Paciente;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CitaServiceTest {

    @Test
    void generaBloquesFijosCadaMediaHora() {
        var bloques = CitaService.bloquesDeMediaHora();
        assertEquals(20, bloques.size());
        assertEquals(LocalTime.of(8, 0), bloques.getFirst());
        assertEquals(LocalTime.of(17, 30), bloques.getLast());
        assertTrue(bloques.stream().allMatch(h -> h.getMinute() == 0 || h.getMinute() == 30));
    }

    @Test
    void rechazaHoraFueraDeBloqueDeMediaHora() {
        CitaService service = new CitaService(new CitaDAO());
        Paciente paciente = new Paciente("Ana", "Gómez", null, null);
        Usuario medico = new Usuario("med", "hash", "Dr. Prueba", Rol.MEDICO);
        Consultorio consultorio = new Consultorio("Consultorio 1");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.agendar(paciente, medico, consultorio, LocalDate.now(), LocalTime.of(9, 15)));
        assertTrue(ex.getMessage().contains("id") || ex.getMessage().contains("Selecciona"));
    }
}
