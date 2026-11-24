package clinica.expediente;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/** Fila de consulta para mostrar el historial del paciente en JavaFX. */
public class AtencionHistorial {
    private final LocalDate fecha;
    private final LocalTime hora;
    private final String motivo;
    private final String diagnostico;
    private final String indicaciones;
    private final String actualizadoPor;
    private final LocalDateTime actualizadoEn;

    public AtencionHistorial(LocalDate fecha, LocalTime hora, String motivo,
                             String diagnostico, String indicaciones,
                             String actualizadoPor, LocalDateTime actualizadoEn) {
        this.fecha = fecha;
        this.hora = hora;
        this.motivo = motivo;
        this.diagnostico = diagnostico;
        this.indicaciones = indicaciones;
        this.actualizadoPor = actualizadoPor;
        this.actualizadoEn = actualizadoEn;
    }

    public LocalDate getFecha() { return fecha; }
    public LocalTime getHora() { return hora; }
    public String getMotivo() { return motivo; }
    public String getDiagnostico() { return diagnostico; }
    public String getIndicaciones() { return indicaciones; }
    public String getActualizadoPor() { return actualizadoPor; }
    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
}
