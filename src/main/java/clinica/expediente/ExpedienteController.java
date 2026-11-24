package clinica.expediente;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;

/** El menú médico debe llamar a configurar() después de cargar este FXML. */
public class ExpedienteController {
    @FXML private TextArea txtMotivo;
    @FXML private TextArea txtDiagnostico;
    @FXML private TextArea txtIndicaciones;
    @FXML private Label lblEstado;
    @FXML private TableView<AtencionHistorial> tablaHistorial;
    @FXML private TableColumn<AtencionHistorial, Object> colFecha;
    @FXML private TableColumn<AtencionHistorial, Object> colHora;
    @FXML private TableColumn<AtencionHistorial, String> colMotivo;
    @FXML private TableColumn<AtencionHistorial, String> colDiagnostico;
    @FXML private TableColumn<AtencionHistorial, String> colIndicaciones;
    @FXML private TableColumn<AtencionHistorial, String> colUsuario;
    @FXML private TableColumn<AtencionHistorial, Object> colActualizado;

    private ExpedienteService expedienteService;
    private int citaId;
    private int usuarioId;

    @FXML
    private void initialize() {
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colMotivo.setCellValueFactory(new PropertyValueFactory<>("motivo"));
        colDiagnostico.setCellValueFactory(new PropertyValueFactory<>("diagnostico"));
        colIndicaciones.setCellValueFactory(new PropertyValueFactory<>("indicaciones"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("actualizadoPor"));
        colActualizado.setCellValueFactory(new PropertyValueFactory<>("actualizadoEn"));
    }

    /** Llamar con el ID de la cita abierta y el ID del médico autenticado. */
    public void configurar(int citaId, int usuarioId) {
        this.expedienteService = new ExpedienteService();
        expedienteService.verificarCita(citaId, usuarioId);
        this.citaId = citaId;
        this.usuarioId = usuarioId;
        cargarAtencionActual();
        cargarHistorial();
    }

    @FXML
    private void guardarAtencion() {
        if (expedienteService == null) {
            mostrarEstado("No se configuró la conexión del expediente.");
            return;
        }
        try {
            expedienteService.guardarAtencion(citaId, txtMotivo.getText(),
                    txtDiagnostico.getText(), txtIndicaciones.getText(), usuarioId);
            mostrarEstado("Atención guardada correctamente.");
            cargarHistorial();
        } catch (RuntimeException ex) {
            mostrarEstado(ex.getMessage() == null ? "No se pudo guardar la atención." : ex.getMessage());
        }
    }

    private void cargarAtencionActual() {
        if (expedienteService == null || citaId <= 0) return;
        try {
            Consulta consulta = expedienteService.obtenerAtencionPorCita(citaId);
            if (consulta != null) {
                txtMotivo.setText(consulta.getMotivo());
                txtDiagnostico.setText(consulta.getDiagnostico());
                txtIndicaciones.setText(consulta.getIndicaciones());
            }
        } catch (RuntimeException ex) {
            mostrarEstado("No se pudo cargar la atención actual: " + ex.getMessage());
        }
    }

    private void cargarHistorial() {
        if (expedienteService == null || citaId <= 0) return;
        try {
            tablaHistorial.setItems(FXCollections.observableArrayList(
                    expedienteService.listarHistorialPorCita(citaId)));
        } catch (RuntimeException ex) {
            mostrarEstado("No se pudo cargar el historial: " + ex.getMessage());
        }
    }

    private void mostrarEstado(String mensaje) {
        if (lblEstado != null) lblEstado.setText(mensaje);
    }
}
