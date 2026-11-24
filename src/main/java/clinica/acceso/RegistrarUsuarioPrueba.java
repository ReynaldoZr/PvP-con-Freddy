package clinica.acceso;

import clinica.database.JPAUtil;

/**
 * Utilidad de línea de comandos para crear un usuario local en PostgreSQL
 * sin guardar contraseñas en el repositorio.
 *
 * Uso:
 *   mvn -q compile exec:java -Dexec.mainClass="clinica.acceso.RegistrarUsuarioPrueba" \
 *       -Dexec.args="usuario clave NombreCompleto RECEPCION|MEDICO"
 */
public class RegistrarUsuarioPrueba {

    public static void main(String[] args) {
        if (args.length < 4) {
            System.err.println("Uso: <usuario> <clave> <nombre> <RECEPCION|MEDICO>");
            System.exit(1);
        }

        String usuario = args[0].trim();
        String clave = args[1];
        String nombre = args[2].trim();
        Rol rol = Rol.valueOf(args[3].trim().toUpperCase());

        UsuarioDAO dao = new UsuarioDAO();
        try {
            if (dao.buscarPorUsuario(usuario).isPresent()) {
                System.out.println("El usuario '" + usuario + "' ya existe en la base de datos.");
                return;
            }
            Usuario nuevo = new Usuario(usuario, SeguridadClave.hash(clave), nombre, rol);
            dao.guardar(nuevo);
            System.out.println("Usuario creado: " + usuario + " (" + rol + ")");
        } finally {
            JPAUtil.cerrar();
        }
    }
}
