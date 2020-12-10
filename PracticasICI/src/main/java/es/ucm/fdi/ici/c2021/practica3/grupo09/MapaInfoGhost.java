package es.ucm.fdi.ici.c2021.practica3.grupo09;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Node;

public class MapaInfoGhost {

	private List<interseccion> mapa = new ArrayList<interseccion>();
	private int ultimoNodo = -1, proximoNodo = -1; // -1 es que aun no ha registrado nada
	private MOVE ultimoMovimientoReal = MOVE.LEFT; // es down por que este programa siempre devuelve down
	private MOVE movimientoDeLlegada = MOVE.RIGHT; // PROVISIONAL tambien
	private interseccion interseccionActual;
	private boolean checkLastMoveMade = false;
	private boolean mapaHecho = false;
	private String mapaActual = "a";

	public EnumMap<GHOST, interseccion> destinosGhosts;
	public EnumMap<GHOST, Integer> movesCheckMate;

	public MapaInfoGhost() {

	}

	public class interseccion {
		public interseccion(int iden, EnumMap<MOVE, Integer> dir, EnumMap<MOVE, Integer> dest,
				EnumMap<MOVE, Integer> pi, EnumMap<MOVE, Integer> ppi) {
			identificador = iden;
			distancias = dir;
			destinos = dest;
			pills = pi;
			powerPill = ppi;
		}

		public int identificador; // node index
		public EnumMap<MOVE, Integer> distancias; // distancias
		public EnumMap<MOVE, Integer> destinos; // identificador del nodo en esa direccion
		public EnumMap<MOVE, Integer> pills; // pills en ese camino (entre nodo y nodo, las pills que hay en las
												// intersecciones no cuentan)
		public EnumMap<MOVE, Integer> powerPill; // powerPills en ese camino
	}

	public void update(Game game){    	
        if(game.getCurrentMaze().name != mapaActual){
			mapaActual = game.getCurrentMaze().name;
			mapa.clear();
			mapaHecho = false;
		}
        
		if (!mapaHecho) { // solo entra aqui en el primer ciclo
			crearMapa(game);
			destinosGhosts = new EnumMap<GHOST, interseccion>(GHOST.class);
			movesCheckMate = new EnumMap<GHOST, Integer>(GHOST.class);
            mapaHecho = true;
		}

		// Primero actualizo el mapa usando la posicion del pacman
		interseccion aux = getInterseccion(game.getPacmanCurrentNodeIndex());
		
		if (aux == null) { // si es null, no estas en una interseccion (AKA, estas en un pasillo)
			if (ultimoNodo != -1 && proximoNodo != -1)
				updateMapa(game); // solo hay que actualizarlo durante las rectas
			if (checkLastMoveMade) {
				MOVE m = game.getPacmanLastMoveMade();
				checkLastMoveMade = false;
				ultimoMovimientoReal = m;
				if(interseccionActual != null) {
					ultimoNodo = interseccionActual.identificador;
					if(interseccionActual.destinos.get(m) != null) {
						proximoNodo = interseccionActual.destinos.get(m);
						movimientoDeLlegada = proxMovimientoLlegada(m);						
					}
				}
			}
		}
		else {
			interseccionActual = aux;
			checkLastMoveMade = true;
		}
    }

    private int[] buscaCamino(Node nodoActual, MOVE dir, Node[] graph) {
		MOVE direccion = dir;
		int pills = 0;
		int powerPills = 0;

		Node proximoNodo = graph[nodoActual.neighbourhood.get(direccion)];
		int coste = 1;

		while ((proximoNodo.numNeighbouringNodes <= 2)) {
			if (proximoNodo.neighbourhood.get(direccion) == null) { // en que otra direccion nos podemos mover
				for (MOVE m : MOVE.values()) { // ya tenemos la nueva direccion
					if (m != direccion.opposite() && proximoNodo.neighbourhood.get(m) != null) {
						direccion = m;
						break;
					}
				}
			}
			if (proximoNodo.pillIndex != -1)
				pills++;
			else if (proximoNodo.powerPillIndex != -1)
				powerPills++;
			proximoNodo = graph[proximoNodo.neighbourhood.get(direccion)];
			coste++;
		}
		return new int[] { coste, proximoNodo.nodeIndex, pills, powerPills };
	}

	private void crearMapa(Game game) {
		Node[] graph = game.getCurrentMaze().graph;

		for (Node nodo : graph) { // recorre todos los nodos del mapa
			if (nodo.numNeighbouringNodes > 2) { // quita muro y pasillos

				// miramos todas las direcciones posibles
				EnumMap<MOVE, Integer> map = nodo.neighbourhood;

				EnumMap<MOVE, Integer> direcciones = new EnumMap<MOVE, Integer>(MOVE.class);
				EnumMap<MOVE, Integer> destinations = new EnumMap<MOVE, Integer>(MOVE.class);
				EnumMap<MOVE, Integer> pills = new EnumMap<MOVE, Integer>(MOVE.class);
				EnumMap<MOVE, Integer> powerPills = new EnumMap<MOVE, Integer>(MOVE.class);

				for (MOVE m : MOVE.values()) {
					if (map.get(m) != null) { // direccion existente
						int[] temp = buscaCamino(nodo, m, graph);
						direcciones.put(m, temp[0]);
						destinations.put(m, temp[1]);
						pills.put(m, temp[2]);
						powerPills.put(m, temp[3]);
					}
				}
				mapa.add(new interseccion(nodo.nodeIndex, direcciones, destinations, pills, powerPills));
			}
		}
	}
	
	private interseccion interseccion_rec(int ini, int fin, int iden) {
		if (fin - ini > 1) { // sigue habiendo varios nodos en el rango de b�squeda
			int mid = (fin - ini) / 2 + ini;
			if (mapa.get(mid).identificador <= iden)
				return interseccion_rec(mid, fin, iden); // esta en el lado derecho
			else
				return interseccion_rec(ini, mid, iden); // esta en el lado izquierdo
		} else if (mapa.get(ini).identificador == iden)
			return mapa.get(ini); // es de tama�o 1 por tanto devuelve el elemento inicial
		else
			return null;
	}

	public interseccion getInterseccion(int iden) { // usa divide y venceras para encontrar la interseccion
		return interseccion_rec(0, mapa.size(), iden);
	}
	
	private void updateMapa(Game game) {
		if (game.wasPillEaten()) {
			interseccion interSalida = getInterseccion(ultimoNodo);
			interseccion interLlegada = getInterseccion(proximoNodo);
			if(interSalida != null) {
					int pills = interSalida.pills.get(ultimoMovimientoReal);
					if(pills > 0) {
						interSalida.pills.replace(ultimoMovimientoReal, pills, pills - 1);
						interLlegada.pills.replace(movimientoDeLlegada, pills, pills - 1);
					}				
			}
		}
		else if (game.wasPowerPillEaten()) {
			interseccion interSalida = getInterseccion(ultimoNodo);
			interseccion interLlegada = getInterseccion(proximoNodo);
			int pills = interSalida.powerPill.get(ultimoMovimientoReal); // no har�a falta esta variable ya que pasaria de 1 a 0,
			interSalida.powerPill.replace(ultimoMovimientoReal, pills, pills - 1); // pero si alguein nos quiere romper el programa poniendo mas de
			interLlegada.powerPill.replace(movimientoDeLlegada, pills, pills - 1); // una powerpill entre dos intersecciones le podemos callar la boca
		}
	}

	private MOVE proxMovimientoLlegada(MOVE proxMove) {
		interseccion interLlegada = getInterseccion(proximoNodo);
		for (MOVE m : MOVE.values()) {
			if (interLlegada.distancias.get(m) != null
					&& interLlegada.destinos.get(m) == interseccionActual.identificador
					&& interLlegada.distancias.get(m) == interseccionActual.distancias.get(proxMove))
				return m;
		}
		return MOVE.NEUTRAL; // nunca deberia llegar
	}

	public interseccion getInterseccionActual() {
		return interseccionActual;
	}

	public boolean getCheckLastModeMade() {
		return checkLastMoveMade;
	}

	public MOVE getUltimoMovReal() {
		return ultimoMovimientoReal;
	}
}
