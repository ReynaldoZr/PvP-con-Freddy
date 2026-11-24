package clinica.acceso;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

/**
 * Controlador del menú principal que habilita módulos exclusivamente según el rol.
 */
public class MenuPrincipalController {

    @FXML
    private Label lblUsuario;

    @FXML
    private Label lblRol;

    @FXML
    private Label lblModuloActual;

    @FXML
    private Label lblDetalle;

    @FXML
    private Button btnPacientes;

    @FXML
    private Button btnCitas;

    @FXML
    private Button btnExpediente;

    @FXML
    private Button btnCobros;

    private final AccesoService accesoService = new AccesoService();
    private Usuario usuarioActual;

    public void inicializarSesion(Usuario usuario) {
        this.usuarioActual = usuario;
        lblUsuario.setText("Usuario: " + usuario.getNombre() + " (" + usuario.getUsuario() + ")");
        lblRol.setText("Rol: " + usuario.getRol());
        aplicarPermisosPorRol(usuario.getRol());
    }

    private void aplicarPermisosPorRol(Rol rol) {
        Map<Modulo, Button> botones = new EnumMap<>(Modulo.class);
        botones.put(Modulo.PACIENTES, btnPacientes);
        botones.put(Modulo.CITAS, btnCitas);
        botones.put(Modulo.EXPEDIENTE, btnExpediente);
        botones.put(Modulo.COBROS, btnCobros);

        for (Map.Entry<Modulo, Button> entry : botones.entrySet()) {
            boolean permitido = rol != null && rol.puedeAbrir(entry.getKey());
            Button boton = entry.getValue();
            boton.setVisible(permitido);
            boton.setManaged(permitido);
            boton.setDisable(!permitido);
        }
    }

    @FXML
    private void onAbrirPacientes() {
        abrirModulo(Modulo.PACIENTES);
    }

    @FXML
    private void onAbrirCitas() {
        abrirModulo(Modulo.CITAS);
    }

    @FXML
    private void onAbrirExpediente() {
        abrirModulo(Modulo.EXPEDIENTE);
    }

    @FXML
    private void onAbrirCobros() {
        abrirModulo(Modulo.COBROS);
    }

    void abrirModulo(Modulo modulo) {
        try {
            accesoService.exigirPermiso(usuarioActual, modulo);
            lblModuloActual.setText("Módulo: " + modulo.getTitulo());
            lblDetalle.setText("Acceso concedido para " + usuarioActual.getRol()
                    + ". Aquí se integrará la vista del módulo " + modulo.getTitulo() + ".");
        } catch (SecurityException ex) {
            lblModuloActual.setText("Acceso denegado");
            lblDetalle.setText(ex.getMessage());
        }
    }

    @FXML
    private void onCerrarSesion() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                MenuPrincipalController.class.getResource("/clinica/acceso/LoginView.fxml"));
        Scene scene = new Scene(loader.load());

        Stage stage = (Stage) lblUsuario.getScene().getWindow();
        stage.setTitle("Clínica - Inicio de sesión");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.centerOnScreen();
    }
}
