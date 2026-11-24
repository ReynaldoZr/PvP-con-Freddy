package clinica.acceso;

import clinica.citas.CitasController;
import clinica.expediente.ExpedienteController;
import clinica.cobros.CobrosController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
    private StackPane contenedorCentro;

    @FXML
    private VBox panelBienvenida;

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
    private final Map<Modulo, Button> botonesPorModulo = new EnumMap<>(Modulo.class);
    private Usuario usuarioActual;

    public void inicializarSesion(Usuario usuario) {
        this.usuarioActual = usuario;
        lblUsuario.setText(usuario.getNombre() + " (" + usuario.getUsuario() + ")");
        lblRol.setText(usuario.getRol().name());
        aplicarPermisosPorRol(usuario.getRol());
    }

    private void aplicarPermisosPorRol(Rol rol) {
        botonesPorModulo.clear();
        botonesPorModulo.put(Modulo.PACIENTES, btnPacientes);
        botonesPorModulo.put(Modulo.CITAS, btnCitas);
        botonesPorModulo.put(Modulo.EXPEDIENTE, btnExpediente);
        botonesPorModulo.put(Modulo.COBROS, btnCobros);

        for (Map.Entry<Modulo, Button> entry : botonesPorModulo.entrySet()) {
            boolean permitido = rol != null && rol.puedeAbrir(entry.getKey());
            Button boton = entry.getValue();
            boton.setVisible(permitido);
            boton.setManaged(permitido);
            boton.setDisable(!permitido);
            boton.getStyleClass().remove("menu-button-active");
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
        abrirModulo(modulo, null);
    }

    public void abrirExpedienteParaCita(Integer citaId) {
        abrirModulo(Modulo.EXPEDIENTE, citaId);
    }

    public void abrirCobrosParaCita(Integer citaId) {
        abrirModulo(Modulo.COBROS, citaId);
    }

    void abrirModulo(Modulo modulo, Integer citaId) {
        try {
            accesoService.exigirPermiso(usuarioActual, modulo);
            marcarBotonActivo(modulo);
            if (modulo == Modulo.PACIENTES) {
                cargarVista("/clinica/pacientes/PacientesView.fxml");
                return;
            }
            if (modulo == Modulo.CITAS) {
                FXMLLoader loader = new FXMLLoader(
                        MenuPrincipalController.class.getResource("/clinica/citas/CitasView.fxml"));
                Parent vista = loader.load();
                CitasController controller = loader.getController();
                controller.configurarContexto(
                        usuarioActual,
                        this::abrirExpedienteParaCita,
                        this::abrirCobrosParaCita);
                contenedorCentro.getChildren().setAll(vista);
                return;
            }
            if (modulo == Modulo.EXPEDIENTE) {
                if (citaId == null) {
                    abrirModulo(Modulo.CITAS);
                    return;
                }
                FXMLLoader loader = new FXMLLoader(MenuPrincipalController.class
                        .getResource("/clinica/expediente/expediente.fxml"));
                Parent vista = loader.load();
                ExpedienteController controller = loader.getController();
                controller.configurar(citaId, usuarioActual.getId());
                contenedorCentro.getChildren().setAll(vista);
                return;
            }
            if (modulo == Modulo.COBROS) {
                FXMLLoader loader = new FXMLLoader(MenuPrincipalController.class
                        .getResource("/clinica/cobros/cobros.fxml"));
                Parent vista = loader.load();
                CobrosController controller = loader.getController();
                controller.configurar(citaId == null ? 0 : citaId);
                contenedorCentro.getChildren().setAll(vista);
                return;
            }
            contenedorCentro.getChildren().setAll(panelBienvenida);
            lblModuloActual.setText("Módulo: " + modulo.getTitulo());
            if (citaId != null) {
                lblDetalle.setText("Cita seleccionada ID: " + citaId
                        + ". Listo para integrarse con el módulo " + modulo.getTitulo() + ".");
            } else {
                lblDetalle.setText("Acceso concedido para " + usuarioActual.getRol()
                        + ". Aquí se integrará la vista del módulo " + modulo.getTitulo() + ".");
            }
        } catch (SecurityException ex) {
            contenedorCentro.getChildren().setAll(panelBienvenida);
            lblModuloActual.setText("Acceso denegado");
            lblDetalle.setText(ex.getMessage());
        } catch (IOException ex) {
            contenedorCentro.getChildren().setAll(panelBienvenida);
            lblModuloActual.setText("Error al abrir módulo");
            lblDetalle.setText("No se pudo cargar la vista de " + modulo.getTitulo() + ".");
        }
    }

    private void cargarVista(String rutaFxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(MenuPrincipalController.class.getResource(rutaFxml));
        Parent vista = loader.load();
        contenedorCentro.getChildren().setAll(vista);
    }

    private void marcarBotonActivo(Modulo moduloActivo) {
        for (Map.Entry<Modulo, Button> entry : botonesPorModulo.entrySet()) {
            Button boton = entry.getValue();
            boton.getStyleClass().remove("menu-button-active");
            if (entry.getKey() == moduloActivo) {
                boton.getStyleClass().add("menu-button-active");
            }
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
