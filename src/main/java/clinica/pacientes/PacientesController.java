package clinica.pacientes;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador para registrar, editar y buscar pacientes usando TableView y Hibernate.
 */
public class PacientesController {

    @FXML
    private TextField txtBuscar;

    @FXML
    private TextField txtNombres;

    @FXML
    private TextField txtApellidos;

    @FXML
    private DatePicker dpFechaNacimiento;

    @FXML
    private TextField txtTelefono;

    @FXML
    private Button btnGuardar;

    @FXML
    private Label lblEstado;

    @FXML
    private TableView<Paciente> tblPacientes;

    @FXML
    private TableColumn<Paciente, Integer> colId;

    @FXML
    private TableColumn<Paciente, String> colNombres;

    @FXML
    private TableColumn<Paciente, String> colApellidos;

    @FXML
    private TableColumn<Paciente, LocalDate> colFechaNacimiento;

    @FXML
    private TableColumn<Paciente, String> colTelefono;

    private final PacienteService pacienteService = new PacienteService();
    private final ObservableList<Paciente> pacientes = FXCollections.observableArrayList();
    private Paciente pacienteSeleccionado;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getId()));
        colNombres.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombres()));
        colApellidos.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getApellidos()));
        colFechaNacimiento.setCellValueFactory(
                data -> new SimpleObjectProperty<>(data.getValue().getFechaNacimiento()));
        colTelefono.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getTelefono() == null ? "" : data.getValue().getTelefono()));

        tblPacientes.setItems(pacientes);
        tblPacientes.getSelectionModel().selectedItemProperty().addListener(
                (obs, anterior, actual) -> cargarEnFormulario(actual));

        cargarPacientes("");
    }

    @FXML
    private void onBuscar() {
        cargarPacientes(txtBuscar.getText());
    }

    @FXML
    private void onLimpiarBusqueda() {
        txtBuscar.clear();
        cargarPacientes("");
    }

    @FXML
    private void onGuardar() {
        try {
            if (pacienteSeleccionado == null) {
                Paciente creado = pacienteService.registrar(
                        txtNombres.getText(),
                        txtApellidos.getText(),
                        dpFechaNacimiento.getValue(),
                        txtTelefono.getText());
                mostrarExito("Paciente registrado (ID " + creado.getId() + ").");
            } else {
                Paciente actualizado = pacienteService.actualizar(
                        pacienteSeleccionado,
                        txtNombres.getText(),
                        txtApellidos.getText(),
                        dpFechaNacimiento.getValue(),
                        txtTelefono.getText());
                mostrarExito("Paciente actualizado (ID " + actualizado.getId() + ").");
            }
            onLimpiarFormulario();
            cargarPacientes(txtBuscar.getText());
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        } catch (Exception ex) {
            mostrarError("No se pudo guardar el paciente en PostgreSQL.");
        }
    }

    @FXML
    private void onLimpiarFormulario() {
        pacienteSeleccionado = null;
        txtNombres.clear();
        txtApellidos.clear();
        dpFechaNacimiento.setValue(null);
        txtTelefono.clear();
        btnGuardar.setText("Registrar paciente");
        tblPacientes.getSelectionModel().clearSelection();
    }

    private void cargarPacientes(String filtro) {
        try {
            List<Paciente> lista = pacienteService.buscar(filtro);
            pacientes.setAll(lista);
            if (lblEstado.getText() == null || lblEstado.getText().isBlank()) {
                lblEstado.setText(lista.size() + " paciente(s) encontrados.");
            }
        } catch (Exception ex) {
            mostrarError("No se pudieron cargar los pacientes.");
        }
    }

    private void cargarEnFormulario(Paciente paciente) {
        this.pacienteSeleccionado = paciente;
        if (paciente == null) {
            return;
        }
        txtNombres.setText(paciente.getNombres());
        txtApellidos.setText(paciente.getApellidos());
        dpFechaNacimiento.setValue(paciente.getFechaNacimiento());
        txtTelefono.setText(paciente.getTelefono() == null ? "" : paciente.getTelefono());
        btnGuardar.setText("Guardar cambios");
        lblEstado.setStyle("");
        lblEstado.setText("Editando paciente ID " + paciente.getId());
    }

    private void mostrarExito(String mensaje) {
        lblEstado.setStyle("-fx-text-fill: #0f766e;");
        lblEstado.setText(mensaje);
    }

    private void mostrarError(String mensaje) {
        lblEstado.setStyle("-fx-text-fill: #b91c1c;");
        lblEstado.setText(mensaje);
    }
}
