package Procesamiento;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Archivo{
    private int filas=1;
    private int col=0;
    private int aux=0;
    
    /*
    ****************************************************************************
            PORT DE LA VERSION ANTERIOR PERO CON PEQUEÑOS CAMBIOS
    ****************************************************************************
    */
    // Constructor
    public Archivo(){
    
    }
    
    // Funcion que lee el mapa en el archivo y lo almacena en un ArrayList bidimencional
    public ArrayList<ArrayList <Integer>> leerArchivo(String direccion){
        direccion="filename.txt"; //Lee el archivo desde la raiz del proyecto
        ArrayList<ArrayList <Integer>>mapa = new ArrayList<ArrayList<Integer>>(); //servira para guardar los datos del terreno 
        mapa.add(new ArrayList<Integer>());
        try{
            FileReader fr=new FileReader(direccion); //Crea el buffer para el archivo
            int terreno= fr.read(); 
            //El while lee hasta el fin del archivo
            while(terreno!=-1){
                /* El IF extrae solo los caracteres validos del archivo 0,1,2,3,4
                   ademas cuenta las filas y las columnas conforme lee el archivo
                   Formato del archivo: 1,0,2,3,1,0,3
                                        2,2,4,0,3,1,1
                
                Nota: no importan las comas pero si los saltos de linea!!!
                */
                if((char)terreno=='0' || (char)terreno=='1'|| (char)terreno=='2'|| (char)terreno=='3'|| (char)terreno=='4'){
                    //System.out.print((int)terreno-'0');
                    mapa.get(filas-1).add((int)terreno-'0');
                    if(aux==0){
                        col++;
                    }  
                }else if((char)terreno=='\n'){
                    //System.out.print("\n");
                    aux=1;
                    filas++;
                    mapa.add(new ArrayList<Integer>());
                }
                terreno=fr.read();
            }
            
            fr.close();
        }catch(IOException e){
            System.out.println("Error en la lectura del archivo");
        }
        return mapa;
    }
    
    /*
        Metodo para guardar el mapa en un archivo de texto
    */
    public void guardarArchivo(ArrayList<ArrayList<Integer>> mapa, String direccion) throws IOException{
        try{
            FileWriter fw = new FileWriter(direccion);
            fw.write("");
            for (int i=0;i<filas;i++){
                for (int j=0;j<col;j++){
                    fw.append(""+mapa.get(i).get(j));
                }
                if (i!=filas-1){
                    fw.append("\n");
                }
            }
            fw.close();
        }catch(IOException e){
            System.out.println("Error en la escritura del archivo");
        }
    }
    
    //get obtener # columas 
    public int getCol(){
        int col=this.col;
        return col;
    }
    //get obtener # filas
    public int getFilas(){
        int filas=this.filas;
        return filas;
    }
}
