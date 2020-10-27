package es.ucm.fdi.ici.c2021.practica0.grupo09;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Vector;

import pacman.controllers.PacmanController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Node;

class interseccion {
	public interseccion(int iden, EnumMap<MOVE, Integer> dir, EnumMap<MOVE, Integer> dest, EnumMap<MOVE, Integer> pi,
			EnumMap<MOVE, Integer> ppi) {
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



public final class MsPacMan extends PacmanController {

	/*Comparator<MOVE> fantasmasComparator = new Comparator<MOVE>() {
		
		@Override
		public int compare(MOVE pos1, MOVE pos2) {
			
		}
	};*/
	
	private List<interseccion> mapa = new ArrayList<interseccion>();
	boolean mapaHecho = false;
	int ultimoNodo = -1, proximoNodo = -1; // -1 es que aun no ha registrado nada
	MOVE ultimoMovimientoReal = MOVE.LEFT; // es down por que este programa siempre devuelve down
	MOVE movimientoDeLlegada = MOVE.RIGHT; // PROVISIONAL tambien
	interseccion interseccionActual;

	double distanciaPeligro = 40;
	double distanciaPerseguir = 50;
	String mapaActual = "a";


	// provisional

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
		if (fin - ini > 1) { // sigue habiendo varios nodos en el rango de búsqueda
			int mid = (fin - ini) / 2 + ini;
			if (mapa.get(mid).identificador <= iden)
				return interseccion_rec(mid, fin, iden); // esta en el lado derecho
			else
				return interseccion_rec(ini, mid, iden); // esta en el lado izquierdo
		} else if (mapa.get(ini).identificador == iden)
			return mapa.get(ini); // es de tamaño 1 por tanto devuelve el elemento inicial
		else
			return null;
	}

	private interseccion getInterseccion(int iden) { // usa divide y venceras para encontrar la interseccion
		return interseccion_rec(0, mapa.size(), iden);
	}

	private void updateMapa(Game game) {
		if (game.wasPillEaten()) {
			interseccion interSalida = getInterseccion(ultimoNodo);
			interseccion interLlegada = getInterseccion(proximoNodo);
			int pills = interSalida.pills.get(ultimoMovimientoReal);
			interSalida.pills.replace(ultimoMovimientoReal, pills, pills - 1);
			interLlegada.pills.replace(movimientoDeLlegada, pills, pills - 1);
		} else if (game.wasPowerPillEaten()) {
			interseccion interSalida = getInterseccion(ultimoNodo);
			interseccion interLlegada = getInterseccion(proximoNodo);
			int pills = interSalida.powerPill.get(ultimoMovimientoReal); // no haría falta esta variable ya que pasaria
																		// de 1 a 0,
			interSalida.pills.replace(ultimoMovimientoReal, pills, pills - 1); // pero si alguein nos quiere romper el
																				// programa poniendo mas de
			interLlegada.pills.replace(movimientoDeLlegada, pills, pills - 1); // una powerpill entre dos intersecciones
																				// le podemos callar la boca
		}
	}

	private boolean isInRisk(Game game) {
		double distanciaAux = distanciaPeligro; //para asegurarnos que si no hay fantasmas cerca, devuelva false
		for (GHOST g : GHOST.values()) {
			double distancia = game.getDistance(interseccionActual.identificador, game.getGhostCurrentNodeIndex(g),
					DM.PATH);
			//si es -1 es que está en la caseta de inicio
			if (distancia != -1 && !game.isGhostEdible(g) && (distanciaAux == 0 || distancia < distanciaAux)) { // si tienes un fantasma cerca que te puedes comer
				distanciaAux = distancia;
			}
		}

		return false;
		//System.out.println(distanciaAux);
		//return distanciaAux < distanciaPeligro;
	}

	private MOVE getBestMove(Game game) {		
	
		Vector<MOVE> fantasmas = new Vector<MOVE>();
		Vector<MOVE> powerPills = new Vector<MOVE>();
		Vector<MOVE> noPills = new Vector<MOVE>();
		Vector<MOVE> pills = new Vector<MOVE>();
		
		for (MOVE m : MOVE.values()) { //no puedes volver para atras
			//mira si m no es de donde vienes, si no es neutral y si existe camino
			if (m != movimientoDeLlegada && m != MOVE.NEUTRAL && interseccionActual.distancias.get(m) != null) {
				
				boolean hasGhost = false;
				//mira para todos los fantasmas, si avanzando por ese camino me pillan
				for (GHOST g : GHOST.values()) {
					double distancia = game.getDistance(interseccionActual.destinos.get(m), game.getGhostCurrentNodeIndex(g),
							DM.PATH);
					if (distancia != -1 && distancia < interseccionActual.distancias.get(m)) { // no pillar el camino						
						hasGhost = true;
						fantasmas.add(m); //por aqui hay fantasma, meterlo a la lista de caminos con fantasmas						
						break; //hacemos el breake por que ya no nos interesa seguir buscando
					}
				}
				
				if(!hasGhost) {
						//mira si hay powerPills (en este momento no estamos en peligro por tanto no nos interesa comerlas)
					if(interseccionActual.powerPill.get(m) > 0)
						powerPills.add(m);
					else if(interseccionActual.pills.get(m)==0)//mira si el camino no tiene pills
						noPills.add(m);
					else pills.add(m); //en el camino solo hay pills
				}
			}
		}	
		
		//Tenemos todas las direcciones almacenadas en los vectores
		boolean movimientoEncontrado = false;
			int aux = 0;
			MOVE actual = MOVE.NEUTRAL;
		
		while(!movimientoEncontrado) {
			for(int i=0;i<pills.size();i++) { //si hay pills
				if(interseccionActual.pills.get(pills.get(i))>aux) {
					aux = interseccionActual.pills.get(pills.get(i));
					actual = pills.elementAt(i);
					movimientoEncontrado = true;
				}
			}		
		}
		aux = -1; //ahora pasa a representar distancias
		while(!movimientoEncontrado) {
			for(int i=0;i<noPills.size();i++) { //si hay pills
				if(aux == -1 || interseccionActual.distancias.get(noPills.get(i)) < aux) {
					aux = interseccionActual.distancias.get(noPills.get(i));
					actual = noPills.elementAt(i);
					movimientoEncontrado = true;
				}
			}
		}
		aux = 0; //ahora pasa a ser powerPills
		while(!movimientoEncontrado) {
			for(int i=0;i<powerPills.size();i++) { //si hay pills
				if(interseccionActual.powerPill.get(powerPills.get(i))>aux) {
					aux = interseccionActual.powerPill.get(powerPills.get(i));
					actual = powerPills.elementAt(i);
					movimientoEncontrado = true;
				}
			}
		}
		aux = -1; //ahora pasa a ser distancias
		while(!movimientoEncontrado) {
			for(int i=0;i<fantasmas.size();i++) { //si hay pills
				if(aux == -1 || interseccionActual.distancias.get(fantasmas.get(i)) < aux) {
					aux = interseccionActual.distancias.get(fantasmas.get(i));
					actual = fantasmas.elementAt(i);
					movimientoEncontrado = true;
				}
			}
		}
		
		
		
		return actual;
	}
	
	GHOST fantasmaComibleCerca(Game game) {
		GHOST fantasma =  null;
		double distancia = 0;
		for (GHOST g : GHOST.values()) {
			if (game.isGhostEdible(g)) {
				double distAux = game.getDistance(interseccionActual.identificador,
						game.getGhostCurrentNodeIndex(g), DM.PATH);
				if (distancia == 0 || distAux < distancia) {
					fantasma = g;
					distancia = distAux;
				}
			}
		}

		if (distancia != 0 || distancia < distanciaPerseguir) { // hay fantasmas para comer y está cerca
			return fantasma;
		} 
		else return null;
	}

	private MOVE mejorDireccion(Game game) {
		GHOST fantasmaCerca = fantasmaComibleCerca(game); //si hay un fantasma comible voy a por el
		if(fantasmaCerca != null) return game.getApproximateNextMoveTowardsTarget(interseccionActual.identificador,
				game.getGhostCurrentNodeIndex(fantasmaCerca), game.getPacmanLastMoveMade(), DM.PATH);
						
		if(isInRisk(game)) { //estoy en riesgo de que me coman
			return MOVE.RIGHT;
		}
		else //no me van a comer
			return getBestMove(game);	
		
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

	@Override
	public MOVE getMove(Game game, long timeDue) {
		
		if(game.getCurrentMaze().name != mapaActual){
			mapaActual = game.getCurrentMaze().name;
			mapa.clear();
			mapaHecho = false;
		}
		if (!mapaHecho) { // solo entra aqui en el primer ciclo
			crearMapa(game);
			mapaHecho = true;

			return MOVE.NEUTRAL; // siempre la primera decision es izquierda abajo
		}

		interseccionActual = getInterseccion(game.getPacmanCurrentNodeIndex());
		if (interseccionActual == null) { // si es null, no estas en una interseccion (AKA, estas en un pasillo)
			if (ultimoNodo != -1 && proximoNodo != -1)
				updateMapa(game); // solo hay que actualizarlo durante las rectas

			return MOVE.NEUTRAL;
		}

		// A PARTIR DE AQUI ESTAS EN UNA INTERSECCION
		MOVE proxMove = mejorDireccion(game);

		ultimoNodo = interseccionActual.identificador;
		proximoNodo = interseccionActual.destinos.get(proxMove);

		ultimoMovimientoReal = proxMove;
		movimientoDeLlegada = proxMovimientoLlegada(proxMove);

		return proxMove; // es para que no salga en rojo
	}
}