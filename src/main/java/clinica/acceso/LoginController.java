package clinica.acceso;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

/**
 * Controlador de la pantalla de inicio de sesión.
 */
public class LoginController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtClave;

    @FXML
    private Label lblError;

    @FXML
    private Button btnIngresar;

    private final AccesoService accesoService = new AccesoService();

    @FXML
    private void initialize() {
        lblError.setText("");
    }

    @FXML
    private void onIngresar() {
        String usuario = txtUsuario.getText() == null ? "" : txtUsuario.getText().trim();
        String clave = txtClave.getText() == null ? "" : txtClave.getText();

        if (usuario.isEmpty() || clave.isEmpty()) {
            lblError.setText("Ingresa usuario y contraseña.");
            return;
        }

        try {
            Optional<Usuario> autenticado = accesoService.autenticar(usuario, clave);
            if (autenticado.isEmpty()) {
                lblError.setText("Usuario o contraseña incorrectos.");
                txtClave.clear();
                return;
            }
            abrirMenuPrincipal(autenticado.get());
        } catch (IllegalStateException ex) {
            lblError.setText(ex.getMessage());
        } catch (Exception ex) {
            lblError.setText("No se pudo conectar con la base de datos. Revisa la configuración.");
        }
    }

    private void abrirMenuPrincipal(Usuario usuario) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                LoginController.class.getResource("/clinica/acceso/MenuPrincipalView.fxml"));
        Scene scene = new Scene(loader.load());

        MenuPrincipalController controller = loader.getController();
        controller.inicializarSesion(usuario);

        Stage stage = (Stage) btnIngresar.getScene().getWindow();
        stage.setTitle("Clínica - Menú principal (" + usuario.getRol() + ")");
        stage.setResizable(true);
        stage.setScene(scene);
        stage.centerOnScreen();
    }
}
