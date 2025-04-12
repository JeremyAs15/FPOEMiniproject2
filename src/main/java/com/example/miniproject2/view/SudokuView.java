package com.example.miniproject2.view;

import com.example.miniproject2.controller.SudokuController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.Optional;

/**
 * Clase que representa la vista del juego y la interacción con el usuario.
 */
public class SudokuView {
    @FXML private GridPane cuadricula;
    @FXML private Button btnNuevoJuego, btnAyuda, btnReiniciar, btnReglas, btnSalir;
    @FXML private Label lblTemporizador, lblJugador, lblMensaje;

    private TextField[][] celdas = new TextField[6][6];
    private SudokuController controlador;

    /**
     * Metodo de inicialización que Configura la cuadrícula y los botones del juego.
     */
    @FXML
    public void initialize() {
        configurarCuadricula();
        configurarBotones();
    }

    /**
     * Resalta y parpadea temporalmente las celdas no iniciales al reiniciar el juego.
     */
    public void resaltarReinicio() {
        for (int fila = 0; fila < 6; fila++) {
            for (int columna = 0; columna < 6; columna++) {
                if (!controlador.esCeldaInicial(fila, columna)) {
                    String estiloBase = (fila / 2 + columna / 3) % 2 == 0 ?
                            "-fx-control-inner-background: #f0f0f0;" :
                            "-fx-control-inner-background: white;";
                    celdas[fila][columna].setStyle(estiloBase + "-fx-background-color: #FFA07A;");
                }
            }
        }

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.3), e -> {
                    for (int fila = 0; fila < 6; fila++) {
                        for (int columna = 0; columna < 6; columna++) {
                            if (!controlador.esCeldaInicial(fila, columna)) {
                                actualizarEstiloCelda(celdas[fila][columna], fila, columna);
                            }
                        }
                    }
                }),
                new KeyFrame(Duration.seconds(0.6), e -> {
                    for (int fila = 0; fila < 6; fila++) {
                        for (int columna = 0; columna < 6; columna++) {
                            if (!controlador.esCeldaInicial(fila, columna)) {
                                String estiloBase = (fila / 2 + columna / 3) % 2 == 0 ?
                                        "-fx-control-inner-background: #f0f0f0;" :
                                        "-fx-control-inner-background: white;";
                                celdas[fila][columna].setStyle(estiloBase + "-fx-background-color: #FFA07A;");
                            }
                        }
                    }
                }),
                new KeyFrame(Duration.seconds(0.9), e -> {
                    for (int fila = 0; fila < 6; fila++) {
                        for (int columna = 0; columna < 6; columna++) {
                            actualizarEstiloCelda(celdas[fila][columna], fila, columna);
                        }
                    }
                })
        );
        timeline.play();
    }

    /**
     * Configura la cuadrícula de 6x6 celdas para el juego.
     */
    private void configurarCuadricula() {
        for (int fila = 0; fila < 6; fila++) {
            for (int columna = 0; columna < 6; columna++) {
                TextField celda = new TextField();
                configurarCelda(celda, fila, columna);
                celdas[fila][columna] = celda;
                cuadricula.add(celda, columna, fila);
            }
        }
    }

    /**
     * Configura el tamaño del texto y su posición, el color de la celda, entre otros parametros
     */
    private void configurarCelda(TextField celda, int fila, int columna) {
        celda.setPrefSize(50, 50);
        celda.setAlignment(javafx.geometry.Pos.CENTER);
        celda.setFont(javafx.scene.text.Font.font(18));
        celda.setEditable(false);

        String estiloBase = (fila / 2 + columna / 3) % 2 == 0 ?
                "-fx-control-inner-background: #f0f0f0;" :
                "-fx-control-inner-background: white;";

        if (controlador != null && controlador.esCeldaInicial(fila, columna)) {
            celda.setStyle(estiloBase + "-fx-font-weight: bold;");
        } else {
            celda.setStyle(estiloBase);
        }

        celda.setOnMouseEntered(e -> {
            if (controlador != null && !controlador.esCeldaInicial(fila, columna)) {
                celda.setStyle(estiloBase + "-fx-background-color: #e6f3ff;");
            }
        });

        celda.setOnMouseExited(e -> actualizarEstiloCelda(celda, fila, columna));

        celda.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("[1-6]?")) {
                celda.setText(oldVal);
            }
        });

        celda.setOnKeyReleased(e -> {
            if (controlador != null && celda.isEditable()) {
                if (!celda.getText().isEmpty()) {
                    controlador.validarEntrada(fila, columna, Integer.parseInt(celda.getText()));
                } else {
                    controlador.validarEntrada(fila, columna, 0);
                }
            }
        });
    }

    /**
     * Actualiza el estilo de las celdas cuando se detecta algun evento sobre ella
     */
    private void actualizarEstiloCelda(TextField celda, int fila, int columna) {
        String estiloBase = (fila / 2 + columna / 3) % 2 == 0 ?
                "-fx-control-inner-background: #f0f0f0;" :
                "-fx-control-inner-background: white;";

        if (controlador != null && controlador.esCeldaInicial(fila, columna)) {
            celda.setStyle(estiloBase + "-fx-font-weight: bold;");
        } else {
            celda.setStyle(estiloBase);
        }
    }

    /**
     * Configura los eventos a realizar por los botones del juego.
     */
    private void configurarBotones() {
        btnNuevoJuego.setOnAction(e -> {
            if (controlador != null) {
                controlador.iniciarNuevoJuego(controlador.getNombreJugador());
            }
        });
        btnAyuda.setOnAction(e -> controlador.solicitarAyuda());
        btnReiniciar.setOnAction(e -> controlador.reiniciarJuego());
        btnReglas.setOnAction(e -> mostrarReglasEnVentana());
        btnSalir.setOnAction(e -> confirmarSalida());
    }

    /**
     * Muestra el diálogo de confirmacion para salir del juego.
     */
    private void confirmarSalida() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar salida");
        alert.setHeaderText(null);
        alert.setContentText("¿Estás seguro que quieres salir del juego?");

        Optional<ButtonType> resultado = alert.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            Stage stage = (Stage) btnSalir.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * Muestra las reglas del juego en la ventana emergente de ayuda.
     */
    private void mostrarReglasEnVentana() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Reglas del Sudoku");
        alert.setHeaderText(null);
        alert.setContentText(controlador.obtenerTextoReglas());
        alert.showAndWait();
    }

    /**
     * Muestra un mensaje en el área de mensajes que se modifica de acuerdo al tipo que se le asigne
     */
    public void mostrarMensaje(String mensaje, String tipo) {
        lblMensaje.setText(mensaje);
        lblMensaje.getStyleClass().removeAll(
                "label-mensaje-inicial",
                "label-mensaje-nuevo-juego",
                "label-mensaje-reinicio",
                "label-mensaje-error",
                "label-mensaje-ayuda",
                "label-mensaje-exito"
        );

        if (tipo != null) {
            lblMensaje.getStyleClass().add("label-mensaje-" + tipo);
        }

        if (tipo != null && !tipo.equals("inicial")) {
            new Timeline(new KeyFrame(
                    Duration.seconds(3),
                    e -> lblMensaje.getStyleClass().remove("label-mensaje-" + tipo)
            )).play();
        }
    }

    /**
     * Muestra el tablero vacío con el mensaje inicial de inicia del juego.
     */
    public void mostrarTableroVacio() {
        limpiarTablero();
        mostrarMensaje("Presione 'Nuevo Juego' para comenzar", "inicial");
    }

    /**
     * Actualiza el valor mostrado en una celda específica posterior a algún cambio en el que se implemente
     */
    public void actualizarCelda(int fila, int columna, int numero) {
        celdas[fila][columna].setText(numero == 0 ? "" : String.valueOf(numero));
    }

    /**
     * Resalta la celda para indicar que se esta ingresando un valor invalido
     */
    public void resaltarError(int fila, int columna) {
        String estiloActual = celdas[fila][columna].getStyle();
        celdas[fila][columna].setStyle(estiloActual + "-fx-border-color: red; -fx-border-width: 2px;");
    }

    /**
     * Resalta una celda con un color específico de acuerdo al uso en que se le vaya a dar
     *
     * @param fila    Fila de la celda (0-5)
     * @param columna Columna de la celda (0-5)
     * @param color   Color en formato hexadecimal (ej. "#FFD700")
     */
    public void resaltarCelda(int fila, int columna, String color) {
        celdas[fila][columna].setStyle("-fx-background-color: " + color + ";");
    }

    /**
     * Limpia todos los resaltados de las celdas.
     */
    public void limpiarResaltados() {
        for (int fila = 0; fila < 6; fila++) {
            for (int columna = 0; columna < 6; columna++) {
                String estiloActual = celdas[fila][columna].getStyle();
                if (estiloActual != null) {
                    celdas[fila][columna].setStyle(
                            estiloActual.replace("-fx-border-color: red;", "")
                                    .replace("-fx-border-width: 2px;", "")
                    );
                }
            }
        }
    }

    /**
     * Limpia todo el tablero, dejando todas las celdas vacías.
     */
    public void limpiarTablero() {
        for (int fila = 0; fila < 6; fila++) {
            for (int columna = 0; columna < 6; columna++) {
                celdas[fila][columna].setText("");
                celdas[fila][columna].setEditable(false);
                actualizarEstiloCelda(celdas[fila][columna], fila, columna);
            }
        }
    }

    /**
     * Muestra el nombre del jugador en la interfaz
     * @param nombre Nombre del jugador a mostrar
     */
    public void mostrarNombreJugador(String nombre) {
        lblJugador.setText("Jugador: " + nombre);
    }

    /**
     * Actualiza el temporizador en la interfaz gráfica
     * @param tiempo Tiempo actualizado
     */
    public void actualizarTemporizador(String tiempo) {
        lblTemporizador.setText("Tiempo: " + tiempo);
    }

    /**
     * Habilita o deshabilita la edición de celdas no iniciales.
     * @param editable true para habilitar edición, false para lo contrario
     */
    public void setCeldasEditables(boolean editable) {
        for (int fila = 0; fila < 6; fila++) {
            for (int columna = 0; columna < 6; columna++) {
                if (controlador != null && !controlador.esCeldaInicial(fila, columna)) {
                    celdas[fila][columna].setEditable(editable);
                }
            }
        }
    }

    /**
     * Establece el controlador para esta vista.
     * @param controlador El controlador del juego Sudoku
     */
    public void setControlador(SudokuController controlador) {
        this.controlador = controlador;
    }
}



