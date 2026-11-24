package clinica.cobros;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

/** Recepción abre esta pantalla pasando el ID de la cita seleccionada. */
public class CobrosController {
    @FXML private Button btnRegistrarCobro;
    @FXML private TextField txtConcepto;
    @FXML private TextField txtImporte;
    @FXML private DatePicker dpFecha;
    @FXML private TableView<CobroDiarioItem> tablaCobros;
    @FXML private TableColumn<CobroDiarioItem, Object> colFechaHora;
    @FXML private TableColumn<CobroDiarioItem, String> colConcepto;
    @FXML private TableColumn<CobroDiarioItem, BigDecimal> colImporte;
    @FXML private Label lblTotal;
    @FXML private Label lblEstado;

    private CobrosService cobrosService;
    private int citaId;
    private final NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "NI"));

    @FXML
    private void initialize() {
        colFechaHora.setCellValueFactory(new PropertyValueFactory<>("registradoEn"));
        colConcepto.setCellValueFactory(new PropertyValueFactory<>("concepto"));
        colImporte.setCellValueFactory(new PropertyValueFactory<>("importe"));
        dpFecha.setValue(LocalDate.now());
    }

    /** Llamar tras cargar el FXML con el ID de la cita seleccionada. */
    public void configurar(int citaId) {
        this.cobrosService = new CobrosService();
        this.citaId = citaId;
        btnRegistrarCobro.setDisable(citaId <= 0);
        txtConcepto.setDisable(citaId <= 0);
        txtImporte.setDisable(citaId <= 0);
        cargarCobrosDelDia();
        if (citaId <= 0) mostrarEstado("Seleccione una cita en la agenda para registrar un cobro.");
    }

    @FXML
    private void registrarCobro() {
        if (cobrosService == null) {
            mostrarEstado("No se configuró la conexión de cobros.");
            return;
        }
        try {
            BigDecimal importe = new BigDecimal(txtImporte.getText().trim());
            cobrosService.registrarCobroDeCita(citaId, txtConcepto.getText(), importe);
            txtConcepto.clear();
            txtImporte.clear();
            dpFecha.setValue(LocalDate.now());
            mostrarEstado("Cobro registrado correctamente.");
            cargarCobrosDelDia();
        } catch (NumberFormatException ex) {
            mostrarEstado("Escriba un importe válido. Ejemplo: 350.00");
        } catch (RuntimeException ex) {
            mostrarEstado(ex.getMessage() == null ? "No se pudo registrar el cobro." : ex.getMessage());
        }
    }

    @FXML
    private void cargarCobrosDelDia() {
        if (cobrosService == null) return;
        LocalDate fecha = dpFecha.getValue() == null ? LocalDate.now() : dpFecha.getValue();
        try {
            var filas = cobrosService.listarCobrosDelDia(fecha);
            tablaCobros.setItems(FXCollections.observableArrayList(filas));
            BigDecimal total = filas.stream().map(CobroDiarioItem::getImporte)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            lblTotal.setText("Total: " + formatoMoneda.format(total));
        } catch (RuntimeException ex) {
            mostrarEstado("No se pudieron cargar los cobros: " + ex.getMessage());
        }
    }

    private void mostrarEstado(String mensaje) {
        if (lblEstado != null) lblEstado.setText(mensaje);
    }
}
