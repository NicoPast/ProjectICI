package es.ucm.fdi.ici.c2021.practica0.grupo09;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Node;

class interseccion {
	
	public interseccion(int iden, EnumMap<MOVE, Integer> dir,EnumMap<MOVE, Integer> pi,EnumMap<MOVE, Integer> ppi) {		
		identificador = iden;
		direccions = dir;
		pills = pi;
		powePill = ppi;
	}
	
	public int identificador; //node index
	public EnumMap<MOVE, Integer> direccions; //direcciones y distancias
	public EnumMap<MOVE, Integer> pills; //pills en ese camino	
	public EnumMap<MOVE, Integer> powePill; //powerPills en ese camino
 }

public final class MsPacMan extends PacmanController {
	
	private List<interseccion> mapa = new ArrayList<interseccion>();
	
	private int[] buscaCamino(Node nodoActual, MOVE dir, Node[] graph) {		
		MOVE direccion = dir;
		int pills = 0;
		int powerPills = 0;
		
		Node proximoNodo = graph[nodoActual.neighbourhood.get(direccion)];
		int coste = 1;
		
		while((proximoNodo.numNeighbouringNodes <= 2)) {
			System.out.println("while");
			if(proximoNodo.neighbourhood.get(direccion) == null) { //en que otra direccion nos podemos mover				
				for(MOVE m: MOVE.values()) { //ya tenemos la nueva direccion
					if(m != direccion.opposite() && proximoNodo.neighbourhood.get(m) != null) { direccion = m; break;}
				}				
			}	
			if(proximoNodo.pillIndex != -1) pills++;
			else if(proximoNodo.powerPillIndex != -1) powerPills++;
			proximoNodo = graph[proximoNodo.neighbourhood.get(direccion)];
			coste++;	
		}	
		
		
		return new int[] {coste, pills, powerPills};
	}
	
	private void crearMapa(Game game) {		
		Node[] graph = game.getCurrentMaze().graph;
		for(Node nodo: graph) { //recorre todos los nodos del mapa
			if(nodo.numNeighbouringNodes > 2) { //quita muro y pasillos
		
				//miramos todas las direcciones posibles
				EnumMap<MOVE, Integer> map = nodo.neighbourhood;

				EnumMap<MOVE,Integer> direcciones = new EnumMap<MOVE, Integer>(MOVE.class);
				EnumMap<MOVE,Integer> pills = new EnumMap<MOVE, Integer>(MOVE.class);
				EnumMap<MOVE,Integer> powerPills = new EnumMap<MOVE, Integer>(MOVE.class);
				
				for(MOVE m:MOVE.values()) {
					if(map.get(m) != null) { //direccion existente
						int[] temp = buscaCamino(nodo, m, graph);
						direcciones.put(m, temp[0]);
						pills.put(m, temp[1]);
						powerPills.put(m, temp[2]);
					}
				}	
				mapa.add(new interseccion(nodo.nodeIndex,direcciones, pills, powerPills));
			}
		}
	}
	
	boolean mapaHecho = false;
	@Override
	public MOVE getMove(Game game, long timeDue) {
		if(!mapaHecho) {
			crearMapa(game);
			System.out.println("llegó");
			mapaHecho = true;
		}
		
       return MOVE.DOWN; //es para que no salga en rojo
    }
}