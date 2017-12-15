/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Procesamiento;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;

/**
 *
 * @author Luis
 */
public class CalcularCaminos extends Thread{
    private Punto[] ini;
    private Punto[] recursos;
    private Personaje[] p;
    Mapa m;
    JPanel panel;
    private String[] marcas = {"x","*","+"}; 
    JTable calculos;
    JButton action;
    
    public CalcularCaminos(Punto[] ini, Punto[] recursos, Personaje[] p, Mapa m, JPanel panel, JTable calculos, JButton action){
        this.ini=ini;
        this.recursos=recursos;
        this.p=p;
        this.m=m;
        this.panel=panel;
        this.calculos=calculos;
        this.action=action;
    }
    
    @Override
    public void run(){
        // Para cada personaje
        for (int i=0;i<p.length;i++){
            //Para cada recurso
            for (int j=0;j<recursos.length-1;j++){
                int pos=0;  //Variable auxiliar para la escritura en la tabla
                if (j==0){
                    pos=1;
                }else if(j==1){
                    pos=3;
                }else{
                    pos=5;
                }
                //Obtiene los JLabels para marcar cada objetivo individual Personaje-Objetivo-Portal
                JLabel jli = m.getEtiqueta(ini[i].y, ini[i].x, panel);
                jli.setText(calculos.getModel().getValueAt(i, 0).toString().charAt(0)+"");
                JLabel jlf = m.getEtiqueta(recursos[j].y, recursos[j].x, panel);
                jlf.setText(calculos.getModel().getColumnName(pos).charAt(2)+"");
                JLabel jld = m.getEtiqueta(recursos[3].y, recursos[3].x, panel);
                jld.setText(calculos.getModel().getColumnName(pos+1).charAt(4)+"");
                boolean run = true; // Nos permite crear un ciclo de espera para que el siguiente hilo no 
                                    // se ejecute hasta que termine el primero
                // Recorrido Personaje-Objetivo
                AEstrella mover = new AEstrella(ini[i],recursos[j],p[i],m,panel,500,marcas[i]);
                mover.start();
                while(run){
                    if (mover.getState()==Thread.State.TERMINATED){
                        run = false;    // Si termina el hilo anterior sale del ciclo
                    }
                }
                calculos.getModel().setValueAt(mover.getPuntaje(), i, pos); // Asigna el costo a la tabla
                calculos.updateUI();    // Actualiza la tabla
                run = true; // Ciclo de espera
                // Recorrido Objetivo-Portal
                AEstrella destino = new AEstrella(recursos[j],recursos[3],p[i],m,panel,500,marcas[i]);
                destino.start();
                while(run){
                    if (destino.getState()==Thread.State.TERMINATED){
                        run = false;    // Si termina el hilo anterior sale del ciclo
                        
                    }
                }
                calculos.getModel().setValueAt(destino.getPuntaje(), i, pos+1);   //Asigna costo a la tabla
                calculos.updateUI();    //Actualiza la tabla
                action.doClick();   // Limpia el mapa
            }
        }
    }
}
