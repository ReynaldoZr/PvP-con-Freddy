package clinica;

import clinica.database.JPAUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Aplicación principal JavaFX que inicia en la pantalla de login.
 */
public class ClinicaApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                ClinicaApplication.class.getResource("/clinica/acceso/LoginView.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Clínica - Inicio de sesión");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void stop() {
        JPAUtil.cerrar();
    }
}
