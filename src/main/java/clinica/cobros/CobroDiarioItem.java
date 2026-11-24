package clinica.cobros;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Fila de la tabla de cobros diarios. */
public class CobroDiarioItem {
    private final LocalDateTime registradoEn;
    private final String concepto;
    private final BigDecimal importe;

    public CobroDiarioItem(LocalDateTime registradoEn, String concepto, BigDecimal importe) {
        this.registradoEn = registradoEn;
        this.concepto = concepto;
        this.importe = importe;
    }

    public LocalDateTime getRegistradoEn() { return registradoEn; }
    public String getConcepto() { return concepto; }
    public BigDecimal getImporte() { return importe; }
}
