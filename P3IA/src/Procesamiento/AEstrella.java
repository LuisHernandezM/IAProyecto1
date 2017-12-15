/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Procesamiento;

import java.awt.ScrollPane;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Luis Hernandez
 */

/*
Clase que ejecuta el algoritmo de estrella, se encuentra como un hilo para poder 
mostrar paso a paso como se va resolviendo el laberinto
*/
public class AEstrella extends Thread{
    Punto ini;
    Punto fin;
    Personaje p;
    Mapa m;
    JTextField txtPuntos;
    ScrollPane spArbol;
    JPanel panel;
    ArrayList<Punto> open;
    ArrayList<Punto> closed;
    Punto pt;
    DefaultMutableTreeNode dm;
    int tiempo;// Causa errores asi que ya no se ocupa, despues la borro
    int puntaje;
    String marca;
    public boolean bandera; 
    
    public AEstrella    (Punto ini, Punto fin, Personaje p, Mapa m, JPanel panel, int tiempo, String marca){
        this.ini=ini;
        this.fin=fin;
        this.p=p;
        this.m=m;
        //this.txtPuntos=txtPuntos;
        //this.spArbol=spArbol;
        this.panel=panel;
        open = new ArrayList<Punto>();
        closed = new ArrayList<Punto>();
        open.add(ini);  // Agrega el nodo inicial al conjunto de nodos abiertos
        pt=new Punto();
        dm = new DefaultMutableTreeNode("Camino:"); // Inicializa el arbol
        tiempo = this.tiempo;
        puntaje = 0;
        this.marca = marca;
        bandera = true;
    }
    
    @Override
    public void run(){
        while(bandera){
            int index = min(open);  //Encuentra el minimo entre los nodos abiertos(open)
            pt = open.get(index);
            open.remove(index); // Remueve el nodo minimo de los nodos abiertos
            /*
            Esta parte del codigo se encarga de mostrar los movimientos
            ********************************************************************
            
            JLabel jl = m.getEtiqueta(pt.y, pt.x, panel);
            if(jl.getText().contains(" ")){
                jl.setText(jl.getText().replace(" ", "o"));
            }else if(jl.getText().contains("o")){
                // Do nothing
            }else{
                jl.setText(jl.getText()+"o");
            }
            panel.updateUI();
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(AEstrella.class.getName()).log(Level.SEVERE, null, ex);
            }
            p.setPuntos(p.getPuntos()+p.getCostoOf(m.getMap().get(pt.y).get(pt.x)));
            txtPuntos.setText(p.getPuntos()+"");
            //*******************************************************************/
            DefaultMutableTreeNode padre=new DefaultMutableTreeNode();
            // Busca si el nodo ya esta en el arbol*
            DefaultMutableTreeNode aux = arbolSearch(dm,new DefaultMutableTreeNode(pt.x+","+pt.y).getUserObject().toString());
            if (aux!=null){
                padre=aux;  // *Si esta lo asigna para modificarlo
            }else{
                padre=new DefaultMutableTreeNode(pt.x+","+pt.y);    //SI no esta lo agrega
                dm.add(padre);
            }
            /*
            Esta parte es la mas importante del algoritmo ya que permite agregar mas nodos a la lista de 
            nodos abiertos, no sin antes validarlos, el algoritmo se basa en el menor f de esta lista, si
            hay coincidencias se basa en el orden como esta el codigo: Arriba, Abajo, Derecha, Izquierda
            */
            // Arriba
            if (pt.y>0){
                Punto up = new Punto(pt.x,pt.y-1);
                if (p.validar(m.getMap().get(up.y).get(up.x))==true){   //  Valida el movimiento, si no se puede hacer lo ignora
                    DefaultMutableTreeNode hijo = new DefaultMutableTreeNode(up.x+","+up.y);    //Si se puede crea el nodo hijo
                    padre.add(hijo);    //  asigna el ijo al padre
                    up.setF(p.getCostoOf(m.getMap().get(up.y).get(up.x))+distancia(up));    //Calcula y asigna f
                    if (up.x == fin.x && up.y == fin.y){    //Si es el punto final:
                        closed.add(up); //Agrega este nodo a la lista de cerrados 
                        //jl = m.getEtiqueta(up.y, up.x, panel);  
                        //jl.setText(jl.getText()+"o");   //Marca como visitado
                        //p.setPuntos(p.getPuntos()+p.getCostoOf(m.getMap().get(up.y).get(up.x)));    // Asigna puntos
                        //txtPuntos.setText(p.getPuntos()+"");
                        try {
                            recorrido(tiempo,marca);    //Manda llamar a la funcion que muestra el recorrido optimo
                        } catch (InterruptedException ex) {
                            Logger.getLogger(AEstrella.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        bandera=false;
                    }else if(search(open,up)!=-1 || search(closed,up)!=-1){
                        // Do Nothing/Add Sentences
                    }else{
                        open.add(up);// Agrega el nodo creado a la lista de nodos abiertos
                    }
                }
            }
            // Abajo
            if (pt.y<m.getMap().size()-1){
                Punto down = new Punto(pt.x,pt.y+1);
                if (p.validar(m.getMap().get(down.y).get(down.x))==true){
                    DefaultMutableTreeNode hijo = new DefaultMutableTreeNode(down.x+","+down.y);
                    padre.add(hijo);
                    down.setF(p.getCostoOf(m.getMap().get(down.y).get(down.x))+distancia(down));
                    if (down.x == fin.x && down.y == fin.y){
                        closed.add(down);
                        //jl = m.getEtiqueta(down.y, down.x, panel);
                        //jl.setText(jl.getText()+"o");
                        //p.setPuntos(p.getPuntos()+p.getCostoOf(m.getMap().get(down.y).get(down.x)));
                        //txtPuntos.setText(p.getPuntos()+"");
                        try {
                            recorrido(tiempo,marca);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(AEstrella.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        bandera=false;
                    }else if(search(open,down)!=-1 || search(closed,down)!=-1){
                        // Do Nothing
                    }else{
                        open.add(down);
                    }
                }
            }
            // Derecha
            if (pt.x<m.getMap().get(0).size()-1){
                Punto right = new Punto(pt.x+1,pt.y);
                if (p.validar(m.getMap().get(right.y).get(right.x))==true){
                    DefaultMutableTreeNode hijo = new DefaultMutableTreeNode(right.x+","+right.y);
                    padre.add(hijo);
                    right.setF(p.getCostoOf(m.getMap().get(right.y).get(right.x))+distancia(right));
                    if (right.x == fin.x && right.y == fin.y){
                        closed.add(right);
                        //jl = m.getEtiqueta(right.y, right.x, panel);
                        //jl.setText(jl.getText()+"o");
                        //p.setPuntos(p.getPuntos()+p.getCostoOf(m.getMap().get(right.y).get(right.x)));
                        //txtPuntos.setText(p.getPuntos()+"");
                        try {
                            recorrido(tiempo,marca);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(AEstrella.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        bandera=false;
                    }else if(search(open,right)!=-1 || search(closed,right)!=-1){
                        // Do Nothing
                    }else{
                        open.add(right);
                    }
                }
            }
            // Izquierda
            if (pt.x>0){
                Punto left = new Punto(pt.x-1,pt.y);
                if (p.validar(m.getMap().get(left.y).get(left.x))==true){
                    DefaultMutableTreeNode hijo = new DefaultMutableTreeNode(left.x+","+left.y);
                    padre.add(hijo);
                    left.setF(p.getCostoOf(m.getMap().get(left.y).get(left.x))+distancia(left));
                    if (left.x == fin.x && left.y == fin.y){
                        closed.add(left);
                        //jl = m.getEtiqueta(left.y, left.x, panel);
                        //jl.setText(jl.getText()+"o");
                        //p.setPuntos(p.getPuntos()+p.getCostoOf(m.getMap().get(left.y).get(left.x)));
                        //txtPuntos.setText(p.getPuntos()+"");
                        try {
                            recorrido(tiempo,marca);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(AEstrella.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        bandera=false;
                    }else if(search(open,left)!=-1 || search(closed,left)!=-1){
                        // Do Nothing
                    }else{
                        open.add(left);
                    }
                }
            }
            if (search(closed,pt)==-1){
                closed.add(pt);//Agrega el nodo que se saco de abiertos a cerrados
            }
            //spArbol.removeAll();    
            //JTree arbol = new JTree(dm);
            //spArbol.add(arbol); //Refresca el ScrollPane que nos muestra el arbol
        }
    }
    
    /* 
    Muestra el camino optimo una vez que se reconocio el mapa, primero
    agrega el camino optimo a un arraylist y despues lo muestra paso a paso 
    recorriendo el arraylist desde el ultimo elemento hasta el primero
    */
    public void recorrido(int tiempo, String marca) throws InterruptedException{
        ArrayList<String> camino = new ArrayList<String>();
        DefaultMutableTreeNode aux = arbolSearch(dm,fin.x+","+fin.y);
        int lim = aux.getLevel();
        for (int j=0; j<lim; j++){
            camino.add(aux.getUserObject().toString());
            aux = (DefaultMutableTreeNode) aux.getParent();
        }
        for (int j=camino.size()-1; j>=0;j--){
            String txt = camino.get(j);
            int i=txt.indexOf(",");
            int x = Integer.parseInt(txt.substring(0, i));
            int y = Integer.parseInt(txt.substring(i+1, txt.length()));
            //System.out.println(txt + "-" + x+","+y);
            if (j!=camino.size()-1){
                puntaje = puntaje + p.getCostoOf(m.getMap().get(y).get(x));
            }
            JLabel jl = m.getEtiqueta(y, x, panel);
            if(jl.getText().contains(marca)){
            
            }else{
                jl.setText(jl.getText() + marca);
            }
            panel.updateUI();
            AEstrella.sleep(50);
        }
        //terminar = true;
    }
    
    public int min(ArrayList<Punto> al){
        int indice=-1;
        int minimo=999999999;
        for (int i=0; i<al.size();i++){
            if (al.get(i).getF()<minimo){
                minimo=al.get(i).getF();
                indice=i;
            }
        }
        return indice;
    }
    
    public int distancia(Punto act){
        return Math.abs(act.x-fin.x)+Math.abs(act.y-fin.y);
    }
    
    public int search(ArrayList<Punto> lista, Punto p){
        int index = -1;
        for (int i=0; i<lista.size(); i++){
            if(lista.get(i).x==p.x && lista.get(i).y==p.y){
                index=i;
            }
        }
        return index;
    }
    
    public DefaultMutableTreeNode arbolSearch(DefaultMutableTreeNode arbol, String search){
        //int index = -1;
        DefaultMutableTreeNode nodo;
        Enumeration e = arbol.breadthFirstEnumeration();
        while(e.hasMoreElements()){
            nodo = (DefaultMutableTreeNode) e.nextElement();
            if (search.equals(nodo.getUserObject().toString())){
                return nodo;
            }
        }
        return null;
    }
    
    public int getPuntaje(){
        return puntaje;
    }
}
