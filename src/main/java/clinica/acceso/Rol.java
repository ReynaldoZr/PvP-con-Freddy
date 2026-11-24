package clinica.acceso;

import java.util.EnumSet;
import java.util.Set;

/**
 * Roles permitidos en la clínica y los módulos a los que cada uno tiene acceso.
 */
public enum Rol {
    RECEPCION(EnumSet.of(Modulo.PACIENTES, Modulo.CITAS, Modulo.COBROS)),
    MEDICO(EnumSet.of(Modulo.CITAS, Modulo.EXPEDIENTE));

    private final Set<Modulo> modulosPermitidos;

    Rol(Set<Modulo> modulosPermitidos) {
        this.modulosPermitidos = modulosPermitidos;
    }

    public boolean puedeAbrir(Modulo modulo) {
        return modulo != null && modulosPermitidos.contains(modulo);
    }
}
