package clinica.acceso;

/**
 * Módulos funcionales de la clínica definidos en BASE.md.
 */
public enum Modulo {
    PACIENTES("Pacientes"),
    CITAS("Citas"),
    EXPEDIENTE("Expediente"),
    COBROS("Cobros");

    private final String titulo;

    Modulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTitulo() {
        return titulo;
    }
}
