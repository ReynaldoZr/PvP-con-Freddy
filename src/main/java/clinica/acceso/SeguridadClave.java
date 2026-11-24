package clinica.acceso;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utilidad para generar y verificar hashes de contraseña con PBKDF2WithHmacSHA256.
 */
public final class SeguridadClave {

    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final String PREFIJO = "pbkdf2";
    private static final int ITERACIONES = 65_536;
    private static final int LONGITUD_CLAVE_BITS = 256;
    private static final int LONGITUD_SALT_BYTES = 16;

    private static final SecureRandom RANDOM = new SecureRandom();

    private SeguridadClave() {
    }

    public static String hash(String clavePlana) {
        if (clavePlana == null || clavePlana.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }
        byte[] salt = new byte[LONGITUD_SALT_BYTES];
        RANDOM.nextBytes(salt);
        byte[] derivado = derivar(clavePlana.toCharArray(), salt);
        Base64.Encoder encoder = Base64.getEncoder();
        return PREFIJO + "$" + encoder.encodeToString(salt) + "$" + encoder.encodeToString(derivado);
    }

    public static boolean verificar(String clavePlana, String claveHashAlmacenada) {
        if (clavePlana == null || clavePlana.isBlank()
                || claveHashAlmacenada == null || claveHashAlmacenada.isBlank()) {
            return false;
        }
        String[] partes = claveHashAlmacenada.split("\\$");
        if (partes.length != 3 || !PREFIJO.equals(partes[0])) {
            return false;
        }
        try {
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] salt = decoder.decode(partes[1]);
            byte[] esperado = decoder.decode(partes[2]);
            byte[] actual = derivar(clavePlana.toCharArray(), salt);
            return MessageDigest.isEqual(actual, esperado);
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private static byte[] derivar(char[] clave, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(clave, salt, ITERACIONES, LONGITUD_CLAVE_BITS);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITMO);
            return factory.generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("No se pudo procesar el hash de la contraseña.", ex);
        } finally {
            spec.clearPassword();
        }
    }
}
