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
 * Controlador principal del juego Sudoku 6x6.
 * Gestiona la interacción con el modelo y la lógica del juego.
 */
public class SudokuController {
    private final SudokuModel modelo;
    private final SudokuView vista;
    private Timeline temporizador;
    private int segundos = 0;
    private String nombreJugador;
    private static final int TAMANO = 6;

    public SudokuController(SudokuModel modelo, SudokuView vista) {
        this.modelo = modelo;
        this.vista = vista;
        this.vista.setControlador(this);
        inicializarTemporizador();
    }

    public void setNombreJugador(String nombre) {
        this.nombreJugador = nombre;
    }

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
            vista.mostrarMensaje("Nuevo juego.", "nuevo-juego");
            temporizador.play();
        }
    }

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
            vista.mostrarMensaje("Juego reiniciado", "reinicio");
            temporizador.play();
        }
    }

    public void validarEntrada(int fila, int columna, int numero) {
        if (esCeldaInicial(fila, columna)) return;

        if (numero == 0) {
            modelo.establecerNumero(fila, columna, 0);
            vista.limpiarResaltados();
            vista.mostrarMensaje("Solo se permiten números del 1 al 6", "error");
            return;
        }

        if (modelo.movimientoValido(fila, columna, numero)) {
            modelo.establecerNumero(fila, columna, numero);
            vista.limpiarResaltados();
            vista.mostrarMensaje("", null);

            if (modelo.juegoCompleto()) {
                temporizador.stop();
                mostrarMensajeFelicitacion();
            }
        } else {
            vista.resaltarError(fila,columna);
            vista.mostrarMensaje("¡Número Inválido!", "error");
        }
    }

    private void mostrarMensajeFelicitacion() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("¡Felicidades!");
        alert.setHeaderText(null);
        alert.setContentText("¡Felicidades " + nombreJugador + "! Has completado el Sudoku en " + formatearTiempo(segundos));
        alert.showAndWait();
    }

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

    private void actualizarVistaCompleta() {
        for (int fila = 0; fila < TAMANO; fila++) {
            for (int columna = 0; columna < TAMANO; columna++) {
                vista.actualizarCelda(fila, columna, modelo.obtenerNumero(fila, columna));
            }
        }
    }

    private void inicializarTemporizador() {
        temporizador = new Timeline(
                new KeyFrame(Duration.seconds(1), evento -> {
                    segundos++;
                    vista.actualizarTemporizador(formatearTiempo(segundos));
                })
        );
        temporizador.setCycleCount(Animation.INDEFINITE);
    }

    private void reiniciarTemporizador() {
        temporizador.stop();
        segundos = 0;
        vista.actualizarTemporizador("00:00");
    }

    public boolean esCeldaInicial(int fila, int columna) {
        return modelo.esCeldaInicial(fila, columna);
    }

    public String getNombreJugador() {
        return nombreJugador;
    }

    public String obtenerTextoReglas() {
        return "REGLAS DEL SUDOKU 6x6:\n\n" +
                "1. Completa la cuadrícula con números del 1 al 6.\n" +
                "2. No repetir números en filas, columnas o bloques 2x3.\n" +
                "3. Los números iniciales no pueden modificarse.\n" +
                "4. Usa el botón 'Sugerir número' para obtener ayuda.";
    }

    private String formatearTiempo(int segundosTotales) {
        int minutos = segundosTotales / 60;
        int segundos = segundosTotales % 60;
        return String.format("%02d:%02d", minutos, segundos);
    }
}