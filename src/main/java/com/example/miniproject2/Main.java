package com.example.miniproject2;

import com.example.miniproject2.controller.SudokuController;
import com.example.miniproject2.model.SudokuModel;
import com.example.miniproject2.view.SudokuView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import java.net.URL;

public class Main extends Application {
    /**
     * Método de inicio.
     *
     * @author Juan Felipe Chapal 2415537 - Jeremy Astaiza 2415667
     * @param escenarioPrincipal El escenario principal que proporciona JavaFX
     * @throws Exception Para el caso en que ocurra un error al momento de cargar el la función "start"
     */
    @Override
    public void start(Stage escenarioPrincipal) throws Exception {
        FXMLLoader cargador = new FXMLLoader(getClass().getResource("/com/example/miniproject2/SudokuView.fxml"));
        Parent raiz = cargador.load();

        SudokuView vista = cargador.getController();
        SudokuModel modelo = new SudokuModel();
        SudokuController controlador = new SudokuController(modelo, vista);

        TextInputDialog dialogo = new TextInputDialog("Jugador");
        dialogo.setTitle("Bienvenido a Sudoku");
        dialogo.setHeaderText("Ingrese su nombre para comenzar");
        dialogo.showAndWait().ifPresent(nombre -> {
            controlador.setNombreJugador(nombre);
            vista.mostrarNombreJugador(nombre);
            vista.mostrarTableroVacio();

            Scene escena = new Scene(raiz, 600, 600);
            URL urlCSS = getClass().getResource("/com/example/miniproject2/view/styles/sudoku.css");
            if (urlCSS != null) escena.getStylesheets().add(urlCSS.toExternalForm());

            escenarioPrincipal.setTitle("Sudoku 6x6");
            escenarioPrincipal.setScene(escena);
            escenarioPrincipal.show();
        });

    }
    /**
     * Método principal de la aplicación.
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        launch(args);
}}
