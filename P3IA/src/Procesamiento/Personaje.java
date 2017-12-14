package Procesamiento;

import java.util.ArrayList;
/*
********************************************************************************
                PORT DE LA VERSION ANTERIOR CON LIGEROS CAMBIOS
********************************************************************************
*/
public class Personaje {
    private static final int MAX_C = 20;    //Maximo costo de un movimiento
    private int puntos;     // Se refiere al monto de puntos gastados
    private int[] costo;    // Costo dependiendo del tipo de zona por personaje
    
    public Personaje(int[] costos){
        puntos = 0;
        costo = new int[5];
        costo[0] = costos[0];      // mountain
        costo[1] = costos[1];      // land
        costo[2] = costos[2];      // water
        costo[3] = costos[3];      // sand
        costo[4] = costos[4];      // forest
    }
    
    /*
    Esta funcion recibe la cadena del tipo de terreno y verifica si los puntos
    para moverse son suficientes; para evitar mayor complejidad si un movimiento
    es invalido debe tener un costo mayor de movimiento que el numero de puntos
    disponibles para moverse.
    Ademas conforme va comprobando suma los puntos de movimientos que se van 
    acumulando para obtener la puntuacion
    */
    public boolean validar(int terreno){
        boolean ret = false; // Si es NULL retorna cero
        if(costo[terreno]<=MAX_C){
            ret=true;         //Movimiento validado
        }
        return ret;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }
    
    public int getCostoOf(int index) {
        return costo[index];
    }
}
