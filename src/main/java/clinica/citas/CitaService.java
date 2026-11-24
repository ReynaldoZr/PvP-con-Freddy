package clinica.citas;

import clinica.acceso.Rol;
import clinica.acceso.Usuario;
import clinica.pacientes.Paciente;
import jakarta.persistence.PersistenceException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Reglas de negocio de la agenda de citas: bloques de 30 minutos y detección de choques.
 */
public class CitaService {

    private final CitaDAO dao;

    public CitaService() {
        this(new CitaDAO());
    }

    public CitaService(CitaDAO dao) {
        this.dao = Objects.requireNonNull(dao);
    }

    public static List<LocalTime> bloquesDeMediaHora() {
        List<LocalTime> bloques = new ArrayList<>();
        for (int hora = 8; hora <= 17; hora++) {
            bloques.add(LocalTime.of(hora, 0));
            bloques.add(LocalTime.of(hora, 30));
        }
        return bloques;
    }

    public List<Usuario> listarMedicos() {
        return dao.listarMedicos();
    }

    public List<Consultorio> listarConsultorios() {
        return dao.listarConsultorios();
    }

    public List<Cita> listarAgenda(LocalDate fecha, Usuario usuarioActual) {
        LocalDate fechaConsulta = fecha != null ? fecha : LocalDate.now();
        if (usuarioActual != null && usuarioActual.getRol() == Rol.MEDICO && usuarioActual.getId() != null) {
            return dao.listarPorFechaYMedico(fechaConsulta, usuarioActual.getId());
        }
        return dao.listarPorFecha(fechaConsulta);
    }

    public Cita agendar(Paciente paciente, Usuario medico, Consultorio consultorio,
                        LocalDate fecha, LocalTime hora) {
        if (paciente == null || paciente.getId() == null) {
            throw new IllegalArgumentException("Selecciona un paciente.");
        }
        if (medico == null || medico.getId() == null || medico.getRol() != Rol.MEDICO) {
            throw new IllegalArgumentException("Selecciona un médico válido.");
        }
        if (consultorio == null || consultorio.getId() == null) {
            throw new IllegalArgumentException("Selecciona un consultorio.");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("Selecciona una fecha para la cita.");
        }
        if (hora == null) {
            throw new IllegalArgumentException("Selecciona un bloque de hora.");
        }
        if ((hora.getMinute() != 0 && hora.getMinute() != 30) || hora.getSecond() != 0) {
            throw new IllegalArgumentException(
                    "La hora debe corresponder a un bloque fijo en :00 o :30.");
        }

        if (dao.existeChoqueMedico(medico.getId(), fecha, hora)) {
            throw new IllegalStateException(
                    "Horario ocupado: el médico " + medico.getNombre()
                            + " ya tiene una cita el " + fecha + " a las " + hora + ".");
        }
        if (dao.existeChoqueConsultorio(consultorio.getId(), fecha, hora)) {
            throw new IllegalStateException(
                    "Horario ocupado: el consultorio " + consultorio.getNombre()
                            + " ya está reservado el " + fecha + " a las " + hora + ".");
        }

        try {
            return dao.guardar(new Cita(paciente, medico, consultorio, fecha, hora));
        } catch (PersistenceException ex) {
            throw new IllegalStateException(
                    "No se pudo agendar la cita porque el médico o el consultorio ya están ocupados en ese bloque.",
                    ex);
        }
    }
}
