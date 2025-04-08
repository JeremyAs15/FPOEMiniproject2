package com.example.miniproject2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class Main extends Application {
    /**
     * Método de inicio.
     *
     * @author Juan Felipe Chapal - Jeremy Astaiza
     * @param escenarioPrincipal El escenario principal que proporciona JavaFX
     * @throws Exception Para el caso en que ocurra un error al momento de cargar el la función "start"
     */
    @Override
    public void start(Stage escenarioPrincipal) throws Exception {

        FXMLLoader cargador = new FXMLLoader(
                getClass().getResource("/com/example/miniproject2/SudokuView.fxml")
        );
        Parent raiz = cargador.load();

        Scene escena = new Scene(raiz, 600, 600);
        URL urlCSS = getClass().getResource("/com/example/miniproject2/styles/sudoku.css");
        if (urlCSS != null) {
            escena.getStylesheets().add(urlCSS.toExternalForm());
        }

        escenarioPrincipal.setTitle("Sudoku 6x6");
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.show();
    }
    /**
     * Método principal de la aplicación.
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        launch(args);
    }
}