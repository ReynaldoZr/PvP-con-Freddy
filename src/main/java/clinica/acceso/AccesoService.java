package clinica.acceso;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Servicio de autenticación y autorización por rol.
 */
public class AccesoService {

    private final Function<String, Optional<Usuario>> buscadorPorUsuario;

    public AccesoService() {
        this(new UsuarioDAO()::buscarPorUsuario);
    }

    public AccesoService(Function<String, Optional<Usuario>> buscadorPorUsuario) {
        this.buscadorPorUsuario = Objects.requireNonNull(buscadorPorUsuario);
    }

    /**
     * Verifica usuario y contraseña. Devuelve el usuario autenticado o vacío si falla.
     */
    public Optional<Usuario> autenticar(String usuario, String clave) {
        if (usuario == null || usuario.isBlank() || clave == null || clave.isBlank()) {
            return Optional.empty();
        }
        Optional<Usuario> encontrado = buscadorPorUsuario.apply(usuario.trim());
        if (encontrado.isEmpty()) {
            return Optional.empty();
        }
        Usuario u = encontrado.get();
        if (!SeguridadClave.verificar(clave, u.getClaveHash())) {
            return Optional.empty();
        }
        return Optional.of(u);
    }

    /**
     * Comprueba que el usuario tenga permiso para abrir el módulo indicado.
     */
    public void exigirPermiso(Usuario usuario, Modulo modulo) {
        if (usuario == null || usuario.getRol() == null || !usuario.getRol().puedeAbrir(modulo)) {
            String nombreModulo = modulo != null ? modulo.getTitulo() : "desconocido";
            throw new SecurityException(
                    "Acceso denegado: tu rol no tiene permiso para abrir " + nombreModulo + ".");
        }
    }
}
