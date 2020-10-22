package es.ucm.fdi.ici.c2021.practica0.grupo09;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Node;

class interseccion {	
	public interseccion(int iden, EnumMap<MOVE, Integer> dir, EnumMap<MOVE, Integer> dest,EnumMap<MOVE, Integer> pi,EnumMap<MOVE, Integer> ppi) {		
		identificador = iden;
		distancias = dir;
		destinos = dest;
		pills = pi;
		powePill = ppi;
	}
	
	public int identificador; //node index
	public EnumMap<MOVE, Integer> distancias; //distancias
	public EnumMap<MOVE, Integer> destinos; //identificador del nodo en esa direccion
	public EnumMap<MOVE, Integer> pills; //pills en ese camino (entre nodo y nodo, las pills que hay en las intersecciones no cuentan)
	public EnumMap<MOVE, Integer> powePill; //powerPills en ese camino
 }

public final class MsPacMan extends PacmanController {
	
	private List<interseccion> mapa = new ArrayList<interseccion>();
	boolean mapaHecho = false;
	int ultimoNodo = -1, proximoNodo = -1; //-1 es que aun no ha registrado nada
	MOVE ultimoMovimientoReal = MOVE.LEFT; //es down por que este programa siempre devuelve down
	MOVE movimientoDeLlegada = MOVE.RIGHT; //PROVISIONAL tambien
	interseccion interseccionActual;
	
	
	//provisional
	
	private int[] buscaCamino(Node nodoActual, MOVE dir, Node[] graph) {		
		MOVE direccion = dir;
		int pills = 0;
		int powerPills = 0;
		
		Node proximoNodo = graph[nodoActual.neighbourhood.get(direccion)];
		int coste = 1;
		
		while((proximoNodo.numNeighbouringNodes <= 2)) {
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
		return new int[] {coste,proximoNodo.nodeIndex, pills, powerPills};
	}
	
	private void crearMapa(Game game) {		
		Node[] graph = game.getCurrentMaze().graph;
		
		for(Node nodo: graph) { //recorre todos los nodos del mapa
			if(nodo.numNeighbouringNodes > 2) { //quita muro y pasillos
		
				//miramos todas las direcciones posibles
				EnumMap<MOVE, Integer> map = nodo.neighbourhood;

				EnumMap<MOVE,Integer> direcciones = new EnumMap<MOVE, Integer>(MOVE.class);
				EnumMap<MOVE,Integer> destinations = new EnumMap<MOVE, Integer>(MOVE.class);
				EnumMap<MOVE,Integer> pills = new EnumMap<MOVE, Integer>(MOVE.class);
				EnumMap<MOVE,Integer> powerPills = new EnumMap<MOVE, Integer>(MOVE.class);
				
				for(MOVE m:MOVE.values()) {
					if(map.get(m) != null) { //direccion existente
						int[] temp = buscaCamino(nodo, m, graph);
						direcciones.put(m, temp[0]);
						destinations.put(m,temp[1]);
						pills.put(m, temp[2]);
						powerPills.put(m, temp[3]);
					}
				}	
				mapa.add(new interseccion(nodo.nodeIndex,direcciones,destinations, pills, powerPills));
			}
		}
	}
	
	private interseccion interseccion_rec(int ini, int fin, int iden) {
		if(fin - ini > 1) { //sigue habiendo varios nodos en el rango de búsqueda
			int mid = (fin-ini)/2 + ini;
			if(mapa.get(mid).identificador <= iden) return interseccion_rec(mid, fin, iden); //esta en el lado derecho
			else return interseccion_rec(ini,mid,iden); //esta en el lado izquierdo
		}
		else if(mapa.get(ini).identificador == iden) return mapa.get(ini); //es de tamaño 1 por tanto devuelve el elemento inicial
		else return null;
	}
	
	private interseccion getInterseccion(int iden) { //usa divide y venceras para encontrar la interseccion
		return interseccion_rec(0,mapa.size(),iden);
	}
	
	private void updateMapa(Game game) {
		if(game.wasPillEaten()) {
			interseccion interSalida = getInterseccion(ultimoNodo);
			interseccion interLlegada= getInterseccion(proximoNodo);			
			int pills = interSalida.pills.get(ultimoMovimientoReal);
			interSalida.pills.replace(ultimoMovimientoReal,pills, pills-1);			
			interLlegada.pills.replace(movimientoDeLlegada, pills, pills-1);
		}
		else if(game.wasPowerPillEaten()) {
			interseccion interSalida = getInterseccion(ultimoNodo);
			interseccion interLlegada= getInterseccion(proximoNodo);			
			int pills = interSalida.powePill.get(ultimoMovimientoReal);       // no haría falta esta variable ya que pasaria de 1 a 0,
			interSalida.pills.replace(ultimoMovimientoReal,pills, pills-1);	  // pero si alguein nos quiere romper el programa poniendo mas de
			interLlegada.pills.replace(movimientoDeLlegada, pills, pills-1);  // una powerpill entre dos intersecciones le podemos callar la boca
		}
	}
	
	
	private MOVE mejorDireccion(Game game) {				
		float proporcion = 0; //numPills/distancia
		MOVE dir = MOVE.NEUTRAL; //la direccion por la cual esta la mejor proporcion
		for(MOVE m: MOVE.values()) {
			if(m != movimientoDeLlegada && m!= MOVE.NEUTRAL &&
					interseccionActual.distancias.get(m) != null) { //no puedes volver hacia atras
				float proporcionAux = (float)interseccionActual.pills.get(m)/(float)interseccionActual.distancias.get(m); //para ahorra calculos
				if(dir == MOVE.NEUTRAL) { //aun no se ha procesado ni una entrada
					proporcion = proporcionAux;
					dir = m;
				}
				else if(proporcionAux > proporcion) { //estamos ante un camino mas factible
					proporcion = proporcionAux;
					dir = m;
				}
			}
		}		
		return dir;
	}
	
	private MOVE proxMovimientoLlegada(MOVE proxMove) {
		interseccion interLlegada= getInterseccion(proximoNodo);
		for(MOVE m: MOVE.values()) {
			if(interLlegada.distancias.get(m) != null &&
					interLlegada.destinos.get(m) == interseccionActual.identificador &&
					interLlegada.distancias.get(m) == interseccionActual.distancias.get(proxMove)) return m;
		}
		
		return MOVE.NEUTRAL; //nunca deberia llegar
	}
	
	@Override
	public MOVE getMove(Game game, long timeDue) {
		if(!mapaHecho) { //solo entra aqui en el primer ciclo
			crearMapa(game);			
			mapaHecho = true;

		    return MOVE.NEUTRAL; //siempre la primera decision es izquierda abajo
		}
		//System.out.println(game.getPacmanCurrentNodeIndex());
		
		interseccionActual = getInterseccion(game.getPacmanCurrentNodeIndex());
		if(interseccionActual == null) { //si es null, no estas en una interseccion (AKA, estas en un pasillo)
			if(ultimoNodo != -1 && proximoNodo != -1)updateMapa(game); //solo hay que actualizarlo durante las rectas
			
			return MOVE.NEUTRAL;
		}
		
		
		//A PARTIR DE AQUI ESTAS EN UNA INTERSECCION
		System.out.println("INTERSECCION");
		
		MOVE proxMove = mejorDireccion(game);
		
		ultimoNodo = interseccionActual.identificador;
		proximoNodo = interseccionActual.destinos.get(proxMove);
		

		ultimoMovimientoReal = proxMove;
		movimientoDeLlegada = proxMovimientoLlegada(proxMove);
	
     		
       return proxMove; //es para que no salga en rojo
    }
}