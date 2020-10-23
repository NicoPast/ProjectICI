package es.ucm.fdi.ici.c2021.practica0.grupo09;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;

import pacman.controllers.GhostController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Node;

public final class Ghosts extends GhostController {
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

 	enum Roles {
		 Perseguidor,
		 Bloqueador,
		 Campeador
	}

	private List<interseccion> mapa = new ArrayList<interseccion>();
	int ultimoNodo = -1, proximoNodo = -1; //-1 es que aun no ha registrado nada
	MOVE ultimoMovimientoReal = MOVE.LEFT; //es down por que este programa siempre devuelve down
	MOVE movimientoDeLlegada = MOVE.RIGHT; //PROVISIONAL tambien
	interseccion interseccionActual;
	boolean checkLastMoveMade = false;

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
		if(fin - ini > 1) { //sigue habiendo varios nodos en el rango de b�squeda
			int mid = (fin-ini)/2 + ini;
			if(mapa.get(mid).identificador <= iden) return interseccion_rec(mid, fin, iden); //esta en el lado derecho
			else return interseccion_rec(ini,mid,iden); //esta en el lado izquierdo
		}
		else if(mapa.get(ini).identificador == iden) return mapa.get(ini); //es de tama�o 1 por tanto devuelve el elemento inicial
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
			int pills = interSalida.powePill.get(ultimoMovimientoReal);       // no har�a falta esta variable ya que pasaria de 1 a 0,
			interSalida.pills.replace(ultimoMovimientoReal,pills, pills-1);	  // pero si alguein nos quiere romper el programa poniendo mas de
			interLlegada.pills.replace(movimientoDeLlegada, pills, pills-1);  // una powerpill entre dos intersecciones le podemos callar la boca
		}
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
	
	boolean mapaHecho = false;
	private EnumMap<GHOST, interseccion> destinosGhosts = new EnumMap<GHOST, interseccion>(GHOST.class);
	private EnumMap<GHOST, MOVE> moves = new EnumMap<GHOST, MOVE>(GHOST.class);
	private MOVE[] allMoves = MOVE.values();
	private Random rnd = new Random();

	@Override
	public final EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {	
		EnumMap<GHOST, MOVE> moves=null;
		if(!mapaHecho) { //solo entra aqui en el primer ciclo
			crearMapa(game);			
			mapaHecho = true;

		    return moves; //siempre la primera decision es izquierda abajo
		}
		
		//Primero actualizo el mapa usando la posicion del pacman
		interseccion aux = getInterseccion(game.getPacmanCurrentNodeIndex());
		if(aux == null) { //si es null, no estas en una interseccion (AKA, estas en un pasillo)
			if(ultimoNodo != -1 && proximoNodo != -1) updateMapa(game); //solo hay que actualizarlo durante las rectas		
			if(checkLastMoveMade) {
				MOVE m = game.getPacmanLastMoveMade();
				checkLastMoveMade = false;
				ultimoMovimientoReal = m;
				movimientoDeLlegada = proxMovimientoLlegada(m);
			}
			return moves;
		}
			
		//A PARTIR DE AQUI ESTAS EN UNA INTERSECCION
		System.out.println("INTERSECCION");

		interseccionActual = aux;
		checkLastMoveMade = true;

		if (isCheckMate(game)) {
			int i;
		}
		else {
			moves = getMovesByRoles(game, getRoles(game));
		}
		return moves;
	}

	private boolean isCheckMate(Game g) {
		return false;
	}

	private EnumMap<GHOST, Roles> getRoles(Game game){
		EnumMap<GHOST, Roles> roles = new EnumMap<GHOST, Roles>(GHOST.class);

		//Hacer cosas

		return roles;
	} 

	private EnumMap<GHOST, MOVE> getMovesByRoles(Game game, EnumMap<GHOST, Roles> roles){
		for (GHOST ghostType : GHOST.values()) {
			if (!game.doesGhostRequireAction(ghostType)) { //Si no se tiene que mover
				continue;
			}
			switch (roles.get(ghostType)) {
				case Perseguidor :
					moves.put(ghostType, getMovePerseguidor(game, ghostType));
					break;
				case Bloqueador :
					moves.put(ghostType, getMoveBloqueador(game, ghostType));
					break;
				case Campeador :
					moves.put(ghostType, getMoveCampeador(game, ghostType));
					break;
				default:
					moves.put(ghostType, MOVE.NEUTRAL); //Random si no se le ha asignado ningun rol
					break;
			}
			//Actualizo la interseccion destino de este fantasma
			destinosGhosts.put(ghostType, getInterseccion(destinosGhosts.get(ghostType).destinos.get(moves.get(ghostType))));
		}
		return moves;
	} 

	//Se dirige a la interseccion donde se dirige MsPacMan
	//Si el pacman esta mas cerca de la pildora que cualquier fantasma, mantengo mis distancias
	private MOVE getMovePerseguidor(Game game, GHOST ghost){
		//La interseccion a la que se dirige el pacman
		interseccion proximaInterseccionPacman = getInterseccion(interseccionActual.destinos.get(ultimoMovimientoReal));

		

		return MOVE.DOWN;
	}

	//Se dirige a una interseccion donde se dirija MsPacMan, evitando ir por el mismo camino que el perseguidor
	//Si el pacman esta mas cerca de la pildora que cualquier fantasma, huyo alejandome de los otros fantamas
	private MOVE getMoveBloqueador(Game game, GHOST ghost){


		return MOVE.DOWN;
	}

	//haga lo que haga, siempre va a estar mas cerca de una pildora que MsPacMan, tratando de acercarse si es posible al Pacman
	//Si el pacman esta mas cerca de la pildora que cualquier fantasma, huyo alejandome de los otros fantamas
	private MOVE getMoveCampeador(Game game, GHOST ghost){


		return MOVE.DOWN;
	}

	//movimiento para huir alejandose tambien de los otros fantamas 
	private MOVE getMoveRunAway(Game game, GHOST ghost){


		return MOVE.DOWN;
	}
}
