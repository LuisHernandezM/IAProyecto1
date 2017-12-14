/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Procesamiento;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author Luis
 */
public class Mapa {
    private Archivo ar = new Archivo();
    private int edit = 0;
    private ArrayList<ArrayList<Integer>> map = new ArrayList<ArrayList<Integer>>();
    /*
    Arreglos  de colores y textos de los Jlabel para manejarlos con un indice
    de esta forma es mas facil manejarlos y se optimiza el codigo al no necesitar 
    validarlo
    */
    private final String[] terreno={"Montaña","Tierra","Agua","Arena","Bosque"};
    private final Color[] background={new Color(77,58,58),new Color(250,191,143),new Color(0,175,255),Color.yellow,new Color(150,210,80)};
    boolean ini;
    boolean fin;
    
    /*
    Getters y Seters
    */
    public ArrayList<ArrayList<Integer>> getMap() {
        return map;
    }

    public Archivo getAr() {
        return ar;
    }

    public void setAr(Archivo ar) {
        this.ar = ar;
    }
    
    public Mapa(String dir){
        map = ar.leerArchivo(dir);
    }

    public int getEdit() {
        return edit;
    }

    public void setEdit(int edit) {
        this.edit = edit;
    }
    
    /*
    Metodo para cargar el mapa a traves del archivo de texto, agrega la opcion
    de carga de terreno a los JLabel, regresa un arreglo de JLabel que planeaba
    usar pero a final de cuentas no se uso
    */
    public JLabel[][] cargarMapa(JPanel mapa) {
        int filas = ar.getFilas();
        int col = ar.getCol();
        int con;
        ini=false;
        fin=false;
        mapa.setLayout(new GridLayout(filas,col));  // columnas y flias de N*M
        JLabel[][] grid = new JLabel[filas][col];
        for (int i=0; i<filas; i++){
            for (int j=0; j<col; j++){
                con = map.get(i).get(j);
                System.out.print(con);
                grid[i][j]=new JLabel();
                grid[i][j].setText(" ");
                grid[i][j].setToolTipText(terreno[con]);
                grid[i][j].setBackground(background[con]);
                grid[i][j].setOpaque(true);
                grid[i][j].setFont(new Font("Courier New",Font.BOLD,21));
                grid[i][j].setHorizontalAlignment(SwingConstants.CENTER);
                grid[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent evt){ // Evento click
                        JLabel jl = new JLabel();
                        jl = (JLabel) evt.getSource();
                        if (edit==1){   // se activa con checkbox Editar
                            // Edicion libre del mapa
                            int indice=-1;
                            for (int i=0;i<background.length;i++){
                                if (jl.getBackground()==background[i]){
                                    indice=i;
                                }
                            }
                            if (indice==background.length-1){
                                indice=0;
                            }else{
                                indice++;
                            }
                            jl.setBackground(background[indice]);
                            jl.setToolTipText(terreno[indice]);
                            mapa.updateUI();
                            // Evento para seleccionar punto inicial y final 
                        } else if (fin==false && evt.isMetaDown()==true){
                            jl.setText("F");
                            fin = true;
                        }else if(ini==false && evt.isMetaDown()==false){
                            jl.setText("I");
                            ini = true;
                        }
                        
                    }
                });
                mapa.add(grid[i][j]);
            }
            System.out.println();
        }
        mapa.updateUI();
        return grid;
    }
    
    /*
    Obtiene todos los elementos del panel para actualizar el arraylist
    bidimensional, para posteriormente guardar el mapa en el archivo de texto
    */
    public void guardarMapa(JPanel mapa) throws IOException{
        int filas = ar.getFilas();
        int col = ar.getCol();
        int c=-1;
        JLabel jl = new JLabel();
        int i=0; int j=0;
        //map.clear();
        //map.add(new ArrayList<Integer>());
        for (Component jc: mapa.getComponents()){
            if (jc instanceof JLabel){
                if (j==col){
                    ++i;
                    j=0;
                    //map.add(new ArrayList<Integer>());
                }
                for (int color=0; color<background.length; color++){
                    if (jc.getBackground()==background[color]){
                        c=color;
                    }
                }
                map.get(i).set(j, c);
                ++j;
            }
        }
        ar.guardarArchivo(map, "filename.txt");
        mapa.updateUI();
    }
    
    /*
    Recorre el panel para dar con el JLabel de la posicion x,y... no es la 
    solucion mas optima pero funciona
    */
    public JLabel getEtiqueta(int fila, int columna, JPanel mapa){
        int col=ar.getCol();
        JLabel jl = new JLabel();
        int i=0; int j=0;
        for (Component jc: mapa.getComponents()){
            if (jc instanceof JLabel){
                if (j==col){
                    ++i;
                    j=0;
                }
                if (j==columna && i==fila){
                    jl = (JLabel) jc;
                    return jl;
                }
                ++j;
            }
        }
        return null;
    }
}

