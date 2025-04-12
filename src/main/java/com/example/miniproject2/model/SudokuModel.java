package com.example.miniproject2.model;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Modelo del juego Sudoku 6x6.
 */
public class SudokuModel {
    private ArrayList<ArrayList<Integer>> tablero;
    private ArrayList<ArrayList<Integer>> tableroInicial;
    private ArrayList<ArrayList<Integer>> solucionCompleta;
    private static final int TAMANO = 6;
    private static final int ANCHO_BLOQUE = 3;
    private static final int ALTO_BLOQUE = 2;

    /**
     * Constructor que inicializa el modelo del Sudoku.
     */
    public SudokuModel() {
        inicializarTablero();
    }

    /**
     * Inicializa las matrices que representan los tableros.
     */
    private void inicializarTablero() {
        tablero = new ArrayList<>(TAMANO);
        tableroInicial = new ArrayList<>(TAMANO);
        solucionCompleta = new ArrayList<>(TAMANO);
        for (int i = 0; i < TAMANO; i++) {
            tablero.add(new ArrayList<>(Collections.nCopies(TAMANO, 0)));
            tableroInicial.add(new ArrayList<>(Collections.nCopies(TAMANO, 0)));
            solucionCompleta.add(new ArrayList<>(Collections.nCopies(TAMANO, 0)));
        }
    }

    /**
     * Genera un nuevo tablero de juego con pistas aleatorias
     * Primero genera una solución completa y luego elimina algunos números para crear el tablero jugable
     */
    public void generarNuevoTablero() {
        generarSolucionCompleta();
        imprimirSolucion();

        for (int fila = 0; fila < TAMANO; fila++) {
            for (int columna = 0; columna < TAMANO; columna++) {
                tablero.get(fila).set(columna, 0);
            }
        }

        for (int fila = 0; fila < 2; fila++) {
            for (int columna = 0; columna < TAMANO; columna++) {
                tablero.get(fila).set(columna, solucionCompleta.get(fila).get(columna));
                tableroInicial.get(fila).set(columna, solucionCompleta.get(fila).get(columna));
            }
        }
        for (int bloqueFila = 0; bloqueFila < TAMANO / ALTO_BLOQUE; bloqueFila++) {
            for (int bloqueCol = 0; bloqueCol < TAMANO / ANCHO_BLOQUE; bloqueCol++) {
                if (bloqueFila * ALTO_BLOQUE < 2) {
                    continue;
                }
                ArrayList<int[]> posiciones = new ArrayList<>();
                for (int i = 0; i < ALTO_BLOQUE; i++) {
                    for (int j = 0; j < ANCHO_BLOQUE; j++) {
                        posiciones.add(new int[]{bloqueFila * ALTO_BLOQUE + i, bloqueCol * ANCHO_BLOQUE + j});
                    }
                }
                Collections.shuffle(posiciones);

                for (int k = 0; k < posiciones.size(); k++) {
                    int[] pos = posiciones.get(k);
                    int fila = pos[0];
                    int columna = pos[1];

                    if (k < 2) {
                        int valor = solucionCompleta.get(fila).get(columna);
                        tablero.get(fila).set(columna, valor);
                        tableroInicial.get(fila).set(columna, valor);
                    } else {
                        tablero.get(fila).set(columna, 0);
                    }
                }
            }
        }
    }


    /**
     * Genera una solución completa válida para el tablero de Sudoku.
     * @return true si se encontró una solución, false en caso contrario
     */
    private boolean generarSolucionCompleta() {
        return resolverTablero(0, 0);
    }

    /**
     * Función que resuelve el tablero antes de iniciar el juego con el objetivo de validar y sugerir el resultado correcto
     * @param fila Fila actual que se está evaluando
     * @param columna Columna actual que se está evaluando
     * @return true si se encontró una solución válida
     */
    private boolean resolverTablero(int fila, int columna) {
        if (fila == TAMANO) return true;
        if (columna == TAMANO) return resolverTablero(fila + 1, 0);
        if (solucionCompleta.get(fila).get(columna) != 0) return resolverTablero(fila, columna + 1);

        ArrayList<Integer> numeros = new ArrayList<>();
        for (int i = 1; i <= TAMANO; i++) numeros.add(i);
        Collections.shuffle(numeros);
        System.out.println(numeros);
        for (int num : numeros) {
            if (movimientoValidoEnSolucion(fila, columna, num)) {
                solucionCompleta.get(fila).set(columna, num);
                if (resolverTablero(fila, columna + 1)) return true;
                solucionCompleta.get(fila).set(columna, 0);
            }
        }
        return false;
    }

    /**
     * Verifica si un número puede colocarse en una posición específica
     * según las reglas del Sudoku para la solución completa.
     * @param fila Fila donde se quiere colocar el número
     * @param columna Columna donde se quiere colocar el número
     * @param numero Número a verificar
     * @return true si el movimiento es válido o false si no lo es, de acuerdo a la soución hecha previamente
     */
    private boolean movimientoValidoEnSolucion(int fila, int columna, int numero) {
        // Verificar fila
        for (int c = 0; c < TAMANO; c++) {
            if (solucionCompleta.get(fila).get(c) == numero) return false;
        }

        // Verificar columna
        for (int r = 0; r < TAMANO; r++) {
            if (solucionCompleta.get(r).get(columna) == numero) return false;
        }

        // Verificar bloque 2x3
        int inicioFilaBloque = fila - fila % ALTO_BLOQUE;
        int inicioColumnaBloque = columna - columna % ANCHO_BLOQUE;

        for (int i = inicioFilaBloque; i < inicioFilaBloque + ALTO_BLOQUE; i++) {
            for (int j = inicioColumnaBloque; j < inicioColumnaBloque + ANCHO_BLOQUE; j++) {
                if (solucionCompleta.get(i).get(j) == numero) return false;
            }
        }

        return true;
    }

    /**
     * Verifica si un número puede colocarse en una posición específica
     * según las reglas del Sudoku para el tablero actual de juego.
     * @param fila Fila donde se quiere colocar el número
     * @param columna Columna donde se quiere colocar el número
     * @param numero Número a verificar
     * @return true si el movimiento es válido, false si no es válido
     */
    public boolean movimientoValido(int fila, int columna, int numero) {
        if (tablero.get(fila).contains(numero)) return false;

        for (int i = 0; i < TAMANO; i++) {
            if (tablero.get(i).get(columna) == numero) return false;
        }

        int inicioFilaBloque = fila - fila % ALTO_BLOQUE;
        int inicioColumnaBloque = columna - columna % ANCHO_BLOQUE;

        for (int i = inicioFilaBloque; i < inicioFilaBloque + ALTO_BLOQUE; i++) {
            for (int j = inicioColumnaBloque; j < inicioColumnaBloque + ANCHO_BLOQUE; j++) {
                if (tablero.get(i).get(j) == numero) return false;
            }
        }

        return true;
    }

    /**
     * Establece un número en una posición específica del tablero.
     * @param fila Fila donde se va a colocar el número
     * @param columna Columna donde se va a colocar el número
     * @param numero Número a colocar
     */
    public void establecerNumero(int fila, int columna, int numero) {
        tablero.get(fila).set(columna, numero);
    }

    /**
     * Obtiene el número en una posición específica del tablero.
     * @param fila Fila del número a obtener
     * @param columna Columna del número a obtener
     * @return El número en la posición especificada
     */
    public int obtenerNumero(int fila, int columna) {
        return tablero.get(fila).get(columna);
    }

    /**
     * Verifica si una celda es parte del tablero inicial
     * @param fila Fila de la celda a verificar
     * @param columna Columna de la celda a verificar
     * @return true si la celda contiene una número inicial, false si no lo tiene
     */
    public boolean esCeldaInicial(int fila, int columna) {
        return tableroInicial.get(fila).get(columna) != 0;
    }

    /**
     * Verificar si el tablero está completo.
     * @return true si está completo y false en caso contrario
     */
    public boolean juegoCompleto() {
        for (int fila = 0; fila < TAMANO; fila++) {
            for (int columna = 0; columna < TAMANO; columna++) {
                if (tablero.get(fila).get(columna) == 0) return false;
            }
        }
        return true;
    }

    /**
     * Obtiene el número correcto para una posición específica según la solución.
     * @param fila Fila de la celda a consultar
     * @param columna Columna de la celda a consultar
     * @return El número correcto para esa posición
     */
    public int obtenerNumeroCorrecto(int fila, int columna) {
        return solucionCompleta.get(fila).get(columna);
    }

    /**
     * Imprime la solución completa del Sudoku en la consola.
     * Método útil para depuración.
     */
    public void imprimirSolucion() {
        System.out.println("Solución completa del Sudoku:");
        for (ArrayList<Integer> fila : solucionCompleta) {
            for (int numero : fila) {
                System.out.print(numero + " ");
            }
            System.out.println();
        }
    }
}