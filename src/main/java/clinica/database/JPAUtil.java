package clinica.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Administra el ciclo de vida del EntityManagerFactory de Hibernate
 * usando las variables de entorno definidas en BASE.md.
 */
public final class JPAUtil {

    private static final String PERSISTENCE_UNIT = "clinicaPU";
    private static EntityManagerFactory emf;

    private JPAUtil() {
    }

    public static synchronized void iniciar() {
        if (emf == null || !emf.isOpen()) {
            String url = exigirVariable("CLINICA_DB_URL");
            String user = exigirVariable("CLINICA_DB_USER");
            String password = exigirVariable("CLINICA_DB_PASSWORD");

            Map<String, Object> properties = new HashMap<>();
            properties.put("jakarta.persistence.jdbc.url", url);
            properties.put("jakarta.persistence.jdbc.user", user);
            properties.put("jakarta.persistence.jdbc.password", password);

            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT, properties);
        }
    }

    public static EntityManager getEntityManager() {
        if (emf == null || !emf.isOpen()) {
            iniciar();
        }
        return emf.createEntityManager();
    }

    public static synchronized void cerrar() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            emf = null;
        }
    }

    private static String exigirVariable(String nombre) {
        String valor = System.getenv(nombre);
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException(
                    "Falta configurar la variable de entorno requerida: " + nombre);
        }
        return valor;
    }
}
