package clinica.cobros;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Cobro único asociado a una consulta atendida. */
@Entity
@Table(name = "cobros")
public class Cobro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "consulta_id", nullable = false, unique = true)
    private Integer consultaId;

    @Column(nullable = false, length = 120)
    private String concepto;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal importe;

    @Column(name = "registrado_en", nullable = false)
    private LocalDateTime registradoEn;

    public Cobro() {
    }

    public Integer getId() { return id; }
    public Integer getConsultaId() { return consultaId; }
    public void setConsultaId(Integer consultaId) { this.consultaId = consultaId; }
    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }
    public BigDecimal getImporte() { return importe; }
    public void setImporte(BigDecimal importe) { this.importe = importe; }
    public LocalDateTime getRegistradoEn() { return registradoEn; }
    public void setRegistradoEn(LocalDateTime registradoEn) { this.registradoEn = registradoEn; }
}
