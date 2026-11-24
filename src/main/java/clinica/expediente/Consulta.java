package clinica.expediente;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/** Atención asociada a una única cita. La cita y el usuario se guardan por ID
 * para reutilizar las tablas compartidas del equipo sin duplicar sus entidades. */
@Entity
@Table(name = "consultas")
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "cita_id", nullable = false, unique = true)
    private Integer citaId;

    @Column(nullable = false, columnDefinition = "text")
    private String motivo;

    @Column(nullable = false, columnDefinition = "text")
    private String diagnostico;

    @Column(columnDefinition = "text")
    private String indicaciones;

    @Column(name = "actualizado_por", nullable = false)
    private Integer actualizadoPorId;

    @Column(name = "actualizado_en", nullable = false)
    private LocalDateTime actualizadoEn;

    public Consulta() {
    }

    public Integer getId() {
        return id;
    }

    public Integer getCitaId() {
        return citaId;
    }

    public void setCitaId(Integer citaId) {
        this.citaId = citaId;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getIndicaciones() {
        return indicaciones;
    }

    public void setIndicaciones(String indicaciones) {
        this.indicaciones = indicaciones;
    }

    public Integer getActualizadoPorId() {
        return actualizadoPorId;
    }

    public void setActualizadoPorId(Integer actualizadoPorId) {
        this.actualizadoPorId = actualizadoPorId;
    }

    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(LocalDateTime actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }
}
