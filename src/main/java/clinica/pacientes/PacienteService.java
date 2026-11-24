package clinica.pacientes;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Reglas de validación y operaciones de negocio para Pacientes.
 */
public class PacienteService {

    private final PacienteDAO dao;

    public PacienteService() {
        this(new PacienteDAO());
    }

    public PacienteService(PacienteDAO dao) {
        this.dao = Objects.requireNonNull(dao);
    }

    public List<Paciente> buscar(String filtro) {
        return dao.buscarPorNombreOApellido(filtro);
    }

    public Paciente registrar(String nombres, String apellidos, LocalDate fechaNacimiento, String telefono) {
        Paciente paciente = validarYConstruir(null, nombres, apellidos, fechaNacimiento, telefono);
        return dao.guardar(paciente);
    }

    public Paciente actualizar(Paciente existente, String nombres, String apellidos,
                               LocalDate fechaNacimiento, String telefono) {
        if (existente == null || existente.getId() == null) {
            throw new IllegalArgumentException("Selecciona un paciente existente para actualizar.");
        }
        validarYConstruir(existente, nombres, apellidos, fechaNacimiento, telefono);
        return dao.actualizar(existente);
    }

    Paciente validarYConstruir(Paciente destino, String nombres, String apellidos,
                               LocalDate fechaNacimiento, String telefono) {
        String nom = nombres == null ? "" : nombres.trim();
        String ape = apellidos == null ? "" : apellidos.trim();
        String tel = telefono == null || telefono.isBlank() ? null : telefono.trim();

        if (nom.isEmpty()) {
            throw new IllegalArgumentException("El campo Nombres es obligatorio.");
        }
        if (nom.length() > 80) {
            throw new IllegalArgumentException("Nombres no puede exceder 80 caracteres.");
        }
        if (ape.isEmpty()) {
            throw new IllegalArgumentException("El campo Apellidos es obligatorio.");
        }
        if (ape.length() > 80) {
            throw new IllegalArgumentException("Apellidos no puede exceder 80 caracteres.");
        }
        if (tel != null && tel.length() > 20) {
            throw new IllegalArgumentException("Teléfono no puede exceder 20 caracteres.");
        }
        if (fechaNacimiento != null && fechaNacimiento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura.");
        }

        Paciente p = destino != null ? destino : new Paciente();
        p.setNombres(nom);
        p.setApellidos(ape);
        p.setFechaNacimiento(fechaNacimiento);
        p.setTelefono(tel);
        return p;
    }
}
