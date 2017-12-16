/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Procesamiento;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableModel;

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
    JTable optimo;
    JButton action;
    
    public CalcularCaminos(Punto[] ini, Punto[] recursos, Personaje[] p, Mapa m, JPanel panel, JTable calculos, JButton action,JTable optimo){
        this.ini=ini;
        this.recursos=recursos;
        this.p=p;
        this.m=m;
        this.panel=panel;
        this.calculos=calculos;
        this.optimo=optimo;
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
        // Seleccion de tareas para personajes
        boolean valido = true;
        TableModel m = calculos.getModel();
        int aux;
        // Crea una matriz auxiliar para detectar si algun personaje no puede realizar todos los movimientos
        int matrix[][] = {{0,0,0},{0,0,0},{0,0,0}};
        // Crea una matriz con los costos excepto los que no se pueden completar
        int costosObj[][] = {{0,0,0},{0,0,0},{0,0,0}};
        int costosPort[][] = {{0,0,0},{0,0,0},{0,0,0}};
        // Verifica que cada personaje pueda al menos cumplir un objetivo
        for (int i=0;i<m.getRowCount();i++){
            aux=0;
            for (int j=1;j<m.getColumnCount();j=j+2){
                
                if (Integer.parseInt(m.getValueAt(i, j).toString())!=0 && Integer.parseInt(m.getValueAt(i, j+1).toString())!=0){
                    matrix[i][aux]=1;
                    costosObj[i][aux]=Integer.parseInt(m.getValueAt(i, j).toString());
                    costosPort[i][aux]=Integer.parseInt(m.getValueAt(i, j+1).toString());
                }
                aux++;
            }
        }
        /*
        for (int i=0;i<3;i++){
            for (int j=0;j<3;j++){
                System.out.print(costosObj[i][j]);
            }
            System.out.println();
        }
        for (int i=0;i<3;i++){
            for (int j=0;j<3;j++){
                System.out.print(costosPort[i][j]);
            }
            System.out.println();
        }
        */
        for (int i=0; i<m.getRowCount();i++){
            if(matrix[0][i]==0 && matrix[1][i]==0 && matrix[2][i]==0 ){
                valido = false;
            }
        }
        if (valido==false){
            JOptionPane.showMessageDialog(panel, "Al menos un personaje no puede llegar a ningun objetivo o portal");
        }else{
            int[][] tareas = getValidos(matrix);
            String[] elementos = {"T","E","L"};
            int[] personajes= {0,0,0};
            personajes[0] = getPersonaje(matrix,costosObj,costosPort,tareas[0][0],-1,-1); 
            personajes[1] = getPersonaje(matrix,costosObj,costosPort,tareas[0][1],personajes[0],-1); 
            personajes[2] = getPersonaje(matrix,costosObj,costosPort,tareas[0][2],personajes[0],personajes[0]);
            TableModel mod = optimo.getModel();
            for (int i=0;i<personajes.length;i++){
                mod.setValueAt(calculos.getModel().getValueAt(i, 0), i, 0);
                mod.setValueAt(elementos[tareas[0][i]], i, 1);
                mod.setValueAt(costosObj[i][tareas[0][i]], i, 2);
                mod.setValueAt(costosPort[i][tareas[0][i]], i, 3);
                JLabel jli = this.m.getEtiqueta(ini[i].y, ini[i].x, panel);
                jli.setText(optimo.getModel().getValueAt(i, 0).toString().charAt(0)+"");
                JLabel jlf = this.m.getEtiqueta(recursos[tareas[0][i]].y, recursos[tareas[0][i]].x, panel);
                jlf.setText(optimo.getModel().getValueAt(i, 1).toString());
            }
            boolean run = true;
            JLabel jlf = this.m.getEtiqueta(recursos[3].y, recursos[3].x, panel);
            jlf.setText("P");
            AEstrella p1 = new AEstrella(ini[0],recursos[tareas[0][0]],p[0],this.m,panel,500,marcas[0]);
            AEstrella p2 = new AEstrella(ini[1],recursos[tareas[0][1]],p[1],this.m,panel,500,marcas[1]);
            AEstrella p3 = new AEstrella(ini[1],recursos[tareas[0][2]],p[2],this.m,panel,500,marcas[2]);
            p1.start();
            p2.start();
            p3.start();
            while(run){
                if (p1.getState()==Thread.State.TERMINATED && p2.getState()==Thread.State.TERMINATED && p3.getState()==Thread.State.TERMINATED){
                    run = false;    // Si termina el hilo anterior sale del ciclo
                }
            }
            AEstrella d1 = new AEstrella(recursos[tareas[0][0]],recursos[3],p[0],this.m,panel,500,marcas[0]);
            AEstrella d2 = new AEstrella(recursos[tareas[0][1]],recursos[3],p[1],this.m,panel,500,marcas[1]);
            AEstrella d3 = new AEstrella(recursos[tareas[0][2]],recursos[3],p[2],this.m,panel,500,marcas[2]);
            d1.start();
            d2.start();
            d3.start();
        }
    }
    
    public int[][] getValidos(int matrix[][]){
        int[][] res = {{0,1,2},{0,0,0}}; //Personaje,MovValidos
        for(int i=0;i<recursos.length-1;i++){
            res[1][i]=matrix[0][i]+matrix[1][i]+matrix[2][i];
        }
        
        // ordenamiento
        int auxpos, auxval;
        for (int i = 0; i < res[0].length-1; i++){      
            for (int j = 0; j < res[0].length-i-1; j++){ 
                if (res[1][j] > res[1][j+1]){
                    auxpos = res[0][j];
                    auxval = res[1][j];
                    res[0][j] = res[0][j+1];
                    res[1][j] = res[1][j+1];
                    res[0][j+1] = auxpos;
                    res[1][j+1] = auxval;
                }
            }
        }
        return res;
    }
    
    public int getPersonaje(int[][] matrix, int[][] costoO,int[][] costoP, int num_tar, int ex, int ex1){
        int personaje = -1;
        int menor = 999999999;
        int[] tar = {matrix[0][num_tar],matrix[1][num_tar],matrix[2][num_tar]};
        int[] obj = {costoO[0][num_tar],costoO[1][num_tar],costoO[2][num_tar]};
        int[] por = {costoP[0][num_tar],costoP[1][num_tar],costoP[2][num_tar]};
        int tam = tar.length;
        if (ex!=-1 && ex1!=-1){
            for (int i=0;i<tam;i++){
                int sum = obj[i]+por[i];
                if (tar[i]!=0 && sum < menor && i!=ex && i!=ex1){
                    menor = sum;
                    personaje = i;
                }
            }
        }else if(ex!=-1 && ex1==-1){
            for (int i=0;i<tam;i++){
                int sum = obj[i]+por[i];
                if (tar[i]!=0 && sum < menor && i!=ex){
                    menor = sum;
                    personaje = i;
                }
            }
        }else{
            for (int i=0;i<tam;i++){
                int sum = obj[i]+por[i];
                if (tar[i]!=0 && sum < menor){
                    menor = sum;
                    personaje = i;
                }
            }
        }
        return personaje;
    }
}
