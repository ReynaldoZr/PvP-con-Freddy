package clinica.citas;

import clinica.acceso.Rol;
import clinica.acceso.Usuario;
import clinica.pacientes.Paciente;
import clinica.pacientes.PacienteService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Consumer;

/**
 * Controlador de agenda de citas por bloques de 30 minutos y paso de citaId a Expediente/Cobros.
 */
public class CitasController {

    @FXML
    private Label lblTituloCitas;

    @FXML
    private HBox filaCampos1;

    @FXML
    private VBox boxHora;

    @FXML
    private ComboBox<Paciente> cmbPaciente;

    @FXML
    private ComboBox<Usuario> cmbMedico;

    @FXML
    private ComboBox<Consultorio> cmbConsultorio;

    @FXML
    private DatePicker dpFecha;

    @FXML
    private ComboBox<LocalTime> cmbHora;

    @FXML
    private Button btnAgendar;

    @FXML
    private Label lblEstado;

    @FXML
    private TableView<Cita> tblCitas;

    @FXML
    private TableColumn<Cita, Integer> colId;

    @FXML
    private TableColumn<Cita, LocalTime> colHora;

    @FXML
    private TableColumn<Cita, String> colPaciente;

    @FXML
    private TableColumn<Cita, String> colMedico;

    @FXML
    private TableColumn<Cita, String> colConsultorio;

    @FXML
    private Button btnAbrirExpediente;

    @FXML
    private Button btnAbrirCobros;

    private final CitaService citaService = new CitaService();
    private final PacienteService pacienteService = new PacienteService();
    private final ObservableList<Cita> citas = FXCollections.observableArrayList();

    private Usuario usuarioActual;
    private Consumer<Integer> onAbrirExpediente;
    private Consumer<Integer> onAbrirCobros;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getId()));
        colHora.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getHora()));
        colPaciente.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getPaciente().getNombreCompleto()));
        colMedico.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getMedico().getNombre()));
        colConsultorio.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getConsultorio().getNombre()));

        tblCitas.setItems(citas);
        cmbHora.setItems(FXCollections.observableArrayList(CitaService.bloquesDeMediaHora()));
        dpFecha.setValue(LocalDate.now());
    }

    public void configurarContexto(Usuario usuario,
                                   Consumer<Integer> onAbrirExpediente,
                                   Consumer<Integer> onAbrirCobros) {
        this.usuarioActual = usuario;
        this.onAbrirExpediente = onAbrirExpediente;
        this.onAbrirCobros = onAbrirCobros;

        boolean esRecepcion = usuario != null && usuario.getRol() == Rol.RECEPCION;
        boolean esMedico = usuario != null && usuario.getRol() == Rol.MEDICO;

        filaCampos1.setVisible(esRecepcion);
        filaCampos1.setManaged(esRecepcion);
        boxHora.setVisible(esRecepcion);
        boxHora.setManaged(esRecepcion);
        btnAgendar.setVisible(esRecepcion);
        btnAgendar.setManaged(esRecepcion);

        btnAbrirCobros.setVisible(esRecepcion);
        btnAbrirCobros.setManaged(esRecepcion);
        btnAbrirExpediente.setVisible(esMedico);
        btnAbrirExpediente.setManaged(esMedico);

        lblTituloCitas.setText(esMedico ? "Mi agenda de citas" : "Agenda de citas");

        if (esRecepcion) {
            cargarCatalogos();
        }
        cargarAgenda();
    }

    @FXML
    private void onCambiarFecha() {
        cargarAgenda();
    }

    @FXML
    private void onRefrescarAgenda() {
        if (usuarioActual != null && usuarioActual.getRol() == Rol.RECEPCION) {
            cargarCatalogos();
        }
        cargarAgenda();
    }

    @FXML
    private void onAgendar() {
        try {
            Cita creada = citaService.agendar(
                    cmbPaciente.getValue(),
                    cmbMedico.getValue(),
                    cmbConsultorio.getValue(),
                    dpFecha.getValue(),
                    cmbHora.getValue());
            mostrarExito("Cita agendada (ID " + creada.getId() + ") para las " + creada.getHora() + ".");
            cargarAgenda();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            mostrarError(ex.getMessage());
        } catch (Exception ex) {
            mostrarError("No se pudo agendar la cita en PostgreSQL.");
        }
    }

    @FXML
    private void onAbrirExpediente() {
        Cita seleccionada = tblCitas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarError("Selecciona una cita de la tabla para abrir Expediente.");
            return;
        }
        if (onAbrirExpediente != null) {
            onAbrirExpediente.accept(seleccionada.getId());
        }
    }

    @FXML
    private void onAbrirCobros() {
        Cita seleccionada = tblCitas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarError("Selecciona una cita de la tabla para abrir Cobros.");
            return;
        }
        if (onAbrirCobros != null) {
            onAbrirCobros.accept(seleccionada.getId());
        }
    }

    private void cargarCatalogos() {
        try {
            cmbPaciente.setItems(FXCollections.observableArrayList(pacienteService.buscar("")));
            cmbMedico.setItems(FXCollections.observableArrayList(citaService.listarMedicos()));
            cmbConsultorio.setItems(FXCollections.observableArrayList(citaService.listarConsultorios()));
        } catch (Exception ex) {
            mostrarError("No se pudieron cargar pacientes, médicos o consultorios.");
        }
    }

    private void cargarAgenda() {
        try {
            LocalDate fecha = dpFecha.getValue() != null ? dpFecha.getValue() : LocalDate.now();
            List<Cita> lista = citaService.listarAgenda(fecha, usuarioActual);
            citas.setAll(lista);
        } catch (Exception ex) {
            mostrarError("No se pudo cargar la agenda de citas.");
        }
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
