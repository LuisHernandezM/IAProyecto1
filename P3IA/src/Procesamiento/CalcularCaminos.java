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
        for (int i=0;i<p.length;i++){
            for (int j=0;j<recursos.length-1;j++){
                int pos=0;
                if (j==0){
                    pos=1;
                }else if(j==1){
                    pos=3;
                }else{
                    pos=5;
                }
                JLabel jli = m.getEtiqueta(ini[i].y, ini[i].x, panel);
                jli.setText(calculos.getModel().getValueAt(i, 0).toString().charAt(0)+"");
                JLabel jlf = m.getEtiqueta(recursos[j].y, recursos[j].x, panel);
                jlf.setText(calculos.getModel().getColumnName(pos).charAt(2)+"");
                JLabel jld = m.getEtiqueta(recursos[3].y, recursos[3].x, panel);
                jld.setText(calculos.getModel().getColumnName(pos+1).charAt(4)+"");
                boolean run = true;
                AEstrella mover = new AEstrella(ini[i],recursos[j],p[i],m,panel,500,marcas[i]);
                mover.start();
                while(run){
                    if (mover.getState()==Thread.State.TERMINATED){
                        run = false;
                    }
                }
                calculos.getModel().setValueAt(mover.getPuntaje(), i, pos);
                calculos.updateUI();
                run = true;
                AEstrella destino = new AEstrella(recursos[j],recursos[3],p[i],m,panel,500,marcas[i]);
                destino.start();
                while(run){
                    if (destino.getState()==Thread.State.TERMINATED){
                        run = false;
                        
                    }
                }
                calculos.getModel().setValueAt(mover.getPuntaje(), i, pos+1);
                calculos.updateUI();
                action.doClick();
            }
        }
    }
}
