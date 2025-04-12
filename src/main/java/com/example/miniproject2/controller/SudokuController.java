package com.example.miniproject2.controller;

import com.example.miniproject2.model.SudokuModel;
import com.example.miniproject2.view.SudokuView;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.util.Duration;
import java.util.Optional;

/**
 * Controlador principal del juego.
 * Gestiona la interacción entre el modelo y la vista),
 * manejando la lógica del juego, validaciones, temporizador y flujo del juego.
 */
public class SudokuController {
    private final SudokuModel modelo;
    private final SudokuView vista;
    private Timeline temporizador;
    private int segundos = 0;
    private String nombreJugador;
    private static final int TAMANO = 6;
    private int contadorNumeroSeis = 0;

    /**
     * Constructor que inicializa el controlador con el modelo y vista proporcionados.
     *
     * @param modelo El modelo del juego
     * @param vista La vista del juego
     */
    public SudokuController(SudokuModel modelo, SudokuView vista) {
        this.modelo = modelo;
        this.vista = vista;
        this.vista.setControlador(this);
        inicializarTemporizador();
    }

    /**
     * Establece el nombre del jugador.
     *
     * @param nombre Nombre del jugador
     */
    public void setNombreJugador(String nombre) {
        this.nombreJugador = nombre;
    }

    /**
     * Inicia un nuevo juego después de confirmación del usuario.
     * Genera un nuevo tablero, reinicia el temporizador y actualiza la vista.
     *
     * @param nombre Nombre del jugador para mostrar en la interfaz
     */
    public void iniciarNuevoJuego(String nombre) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar nuevo juego");
        alert.setHeaderText(null);
        alert.setContentText("¿Estás seguro que quieres comenzar un nuevo juego?");

        Optional<ButtonType> resultado = alert.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            modelo.generarNuevoTablero();
            reiniciarTemporizador();
            vista.mostrarNombreJugador(nombreJugador);
            actualizarVistaCompleta();
            vista.setCeldasEditables(true);
            vista.limpiarResaltados();
            vista.mostrarMensaje("¡Nuevo juego!", "nuevo-juego");
            temporizador.play();
        }
    }

    /**
     * Reinicia el juego actual después de confirmación del usuario.
     * Limpia todas las celdas no iniciales y reinicia el temporizador.
     */
    public void reiniciarJuego() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar reinicio");
        alert.setHeaderText(null);
        alert.setContentText("¿Estás seguro que quieres reiniciar el juego? Se perderá tu progreso actual.");

        Optional<ButtonType> resultado = alert.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            for (int fila = 0; fila < TAMANO; fila++) {
                for (int columna = 0; columna < TAMANO; columna++) {
                    if (!esCeldaInicial(fila, columna)) {
                        modelo.establecerNumero(fila, columna, 0);
                    }
                }
            }
            vista.resaltarReinicio();
            actualizarVistaCompleta();
            reiniciarTemporizador();
            vista.setCeldasEditables(true);
            vista.limpiarResaltados();
            vista.mostrarMensaje("¡Juego reiniciado!", "reinicio");
            temporizador.play();
        }
    }

    /**
     * Valida una entrada del usuario en una celda específica.
     *
     * @param fila Índice de la fila (0-5)
     * @param columna Índice de la columna (0-5)
     * @param numero Número ingresado por el usuario (0-6)
     */
    public void validarEntrada(int fila, int columna, int numero) {
        if (esCeldaInicial(fila, columna)) return;

        if (numero == 0) {
            modelo.establecerNumero(fila, columna, 0);
            vista.limpiarResaltados();
            vista.mostrarMensaje("Solo se permiten números del 1 al 6", "error");
            if (modelo.obtenerNumero(fila, columna) == 6) {
                actualizarContadorNumeroSeis();
            }
            return;
        }

        if (modelo.movimientoValido(fila, columna, numero)) {
            modelo.establecerNumero(fila, columna, numero);
            vista.limpiarResaltados();
            vista.mostrarMensaje("", null);

            if (numero == 6 || modelo.obtenerNumero(fila, columna) == 6 ) {
                actualizarContadorNumeroSeis();
            }

            if (modelo.juegoCompleto()) {
                temporizador.stop();
                mostrarMensajeFelicitacion();
            }
        } else {
            vista.resaltarError(fila,columna);
            vista.mostrarMensaje("¡Número Inválido!", "error");
        }
    }

    /**
     * Muestra un mensaje de felicitación cuando el juego se completa.
     */
    private void mostrarMensajeFelicitacion() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("¡Felicidades!");
        alert.setHeaderText(null);
        alert.setContentText("¡Felicidades " + nombreJugador + "! Has completado el Sudoku en " + formatearTiempo(segundos));
        alert.showAndWait();
    }

    /**
     * Proporciona ayuda al jugador mostrando el número correcto para la primera celda vacía encontrada.
     */
    public void solicitarAyuda() {
        for (int fila = 0; fila < TAMANO; fila++) {
            for (int columna = 0; columna < TAMANO; columna++) {
                if (modelo.obtenerNumero(fila, columna) == 0) {
                    int numeroCorrecto = modelo.obtenerNumeroCorrecto(fila, columna);
                    vista.mostrarMensaje(
                            String.format("Número correcto: %d", numeroCorrecto), "ayuda"
                    );
                    vista.resaltarCelda(fila, columna, "#FFD700");
                    return;
                }
            }
        }
        vista.mostrarMensaje("¡Sudoku Completado!", "exito");
    }

    /**
     * Actualiza toda la vista con los valores actuales del modelo.
     */
    private void actualizarVistaCompleta() {
        for (int fila = 0; fila < TAMANO; fila++) {
            for (int columna = 0; columna < TAMANO; columna++) {
                vista.actualizarCelda(fila, columna, modelo.obtenerNumero(fila, columna));
            }
        }
    }

    public void actualizarContadorNumeroSeis() {
        int contadorNumeroSeis = 0;
        for (int fila = 0; fila < TAMANO; fila++) {
            for (int columna = 0; columna < TAMANO; columna++) {
                if (modelo.obtenerNumero(fila, columna) == 6) {
                    contadorNumeroSeis++;
                }
            }
        }
        vista.actualizarNumeroSeis(contadorNumeroSeis);
    }

    /**
     * Inicializa el temporizador del juego que se ejecuta cada segundo.
     */
    private void inicializarTemporizador() {
        temporizador = new Timeline(
                new KeyFrame(Duration.seconds(1), evento -> {
                    segundos++;
                    vista.actualizarTemporizador(formatearTiempo(segundos));
                })
        );
        temporizador.setCycleCount(Animation.INDEFINITE);
    }

    /**
     * Reinicia el temporizador del juego a cero.
     */
    private void reiniciarTemporizador() {
        temporizador.stop();
        segundos = 0;
        vista.actualizarTemporizador("00:00");
    }

    /**
     * Verifica si una celda es parte del tablero inicial (no editable).
     *
     * @param fila Índice de la fila
     * @param columna Índice de la columna
     * @return true si la celda es parte del tablero inicial, false en caso contrario
     */
    public boolean esCeldaInicial(int fila, int columna) {
        return modelo.esCeldaInicial(fila, columna);
    }

    /**
     * Obtiene el nombre del jugador actual.
     *
     * @return Nombre del jugador
     */
    public String getNombreJugador() {
        return nombreJugador;
    }

    /**
     * Devuelve las reglas del juego formateadas como texto.
     *
     * @return String con las reglas del juego
     */
    public String obtenerTextoReglas() {
        return "REGLAS DEL SUDOKU 6x6:\n\n" +
                "1. Completa la cuadrícula con números del 1 al 6.\n" +
                "2. No repetir números en filas, columnas o bloques 2x3.\n" +
                "3. Los números iniciales no pueden modificarse.\n" +
                "4. Usa el botón 'Sugerir número' para obtener ayuda.";
    }

    /**
     * Formatea un tiempo en segundos a formato MM:SS.
     *
     * @param segundosTotales Tiempo total en segundos
     * @return String formateado como MM:SS
     */
    private String formatearTiempo(int segundosTotales) {
        int minutos = segundosTotales / 60;
        int segundos = segundosTotales % 60;
        return String.format("%02d:%02d", minutos, segundos);
    }
}