package es.ucm.fdi.ici.c2021.practica0.grupo09;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import pacman.controllers.GhostController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Node;

public final class Ghosts extends GhostController {
	class interseccion {
		public interseccion(int iden, EnumMap<MOVE, Integer> dir, EnumMap<MOVE, Integer> dest,
				EnumMap<MOVE, Integer> pi, EnumMap<MOVE, Integer> ppi) {
			identificador = iden;
			distancias = dir;
			destinos = dest;
			pills = pi;
			powePill = ppi;
		}

		public int identificador; // node index
		public EnumMap<MOVE, Integer> distancias; // distancias
		public EnumMap<MOVE, Integer> destinos; // identificador del nodo en esa direccion
		public EnumMap<MOVE, Integer> pills; // pills en ese camino (entre nodo y nodo, las pills que hay en las intersecciones no cuentan)
		public EnumMap<MOVE, Integer> powePill; // powerPills en ese camino
	}

	enum Roles {
		Perseguidor, Campeador
	}

	private List<interseccion> mapa = new ArrayList<interseccion>();
	int ultimoNodo = -1, proximoNodo = -1; // -1 es que aun no ha registrado nada
	MOVE ultimoMovimientoReal = MOVE.LEFT; // es down por que este programa siempre devuelve down
	MOVE movimientoDeLlegada = MOVE.RIGHT; // PROVISIONAL tambien
	interseccion interseccionActual;
	boolean checkLastMoveMade = false;

	double CONSTANT_ROL_CAMPEADOR = 15;
	int CONSTANT_CAMPEADOR_ERROR = 5;
	int CONSTANT_LIMITE_HUIDA_PERSEGUIDOR = 25;
	int CONSTANT_LIMITE_HUIDA_CAMPEADOR = 25;
	int CONSTANT_LIMITE_MULTIPLIER = 2;

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
			int pills = interSalida.powePill.get(ultimoMovimientoReal); // no har�a falta esta variable ya que pasaria de 1 a 0,
			interSalida.pills.replace(ultimoMovimientoReal, pills, pills - 1); // pero si alguein nos quiere romper el programa poniendo mas de
			interLlegada.pills.replace(movimientoDeLlegada, pills, pills - 1); // una powerpill entre dos intersecciones le podemos callar la boca
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

	boolean mapaHecho = false;
	private EnumMap<GHOST, interseccion> destinosGhosts = new EnumMap<GHOST, interseccion>(GHOST.class);
	private EnumMap<GHOST, MOVE> moves = new EnumMap<GHOST, MOVE>(GHOST.class);

	@Override
	public final EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		EnumMap<GHOST, MOVE> moves = null;
		if (!mapaHecho) { // solo entra aqui en el primer ciclo
			crearMapa(game);
			mapaHecho = true;

			return moves; // siempre la primera decision es izquierda abajo
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
				//movimientoDeLlegada = proxMovimientoLlegada(m);
			}
		}
		else {
			interseccionActual = aux;
			checkLastMoveMade = true;
		}

		if (isCheckMate(game)) {
			// Functionality in isCheckmate()
		} else {
			getMovesByRoles(game, getRoles(game));
		}
		return moves;
	}

	private class GHOSTANDDISTANCE {
		public GHOST ghost = null;
		public double distance = Double.MAX_VALUE;
	}

	private GHOSTANDDISTANCE closestGhostToIntersection(Game game, interseccion inter, Vector<GHOST> libres) {
		GHOSTANDDISTANCE gyd = new GHOSTANDDISTANCE();
		for (GHOST ghost : libres) {
			double aux = game.getDistance(inter.identificador, game.getGhostCurrentNodeIndex(ghost), game.getGhostLastMoveMade(ghost), DM.PATH);
			if (aux < gyd.distance) {
				gyd.ghost = ghost;
				gyd.distance = aux;
			}
		}
		return gyd;
	}

	private void rellenarProxDestinos(Set<interseccion> inters, Vector<Integer> nodosFijos, Game g) {
		if (inters.isEmpty() && !checkLastMoveMade){
			inters.add(getInterseccion(interseccionActual.destinos.get(ultimoMovimientoReal)));
		}
		else {
			Set<interseccion> aux = new HashSet<interseccion>(inters);
			inters.clear();
			// Buscamos las intersecciones correspondientes a los movimientos posibles
			// y si ya está esa intersección fijada no expandimos su rama
			for (interseccion a : aux) {
				if (!nodosFijos.contains(a.identificador)) {
					for (MOVE mo : a.destinos.keySet()) {
						interseccion interse = getInterseccion(a.destinos.get(mo));
						if (interse != null) {
							inters.add(interse);
						}
					}
				}
			}
		}
	}

	private Vector<GHOST> activeGhosts(Game game){
		Vector<GHOST> activeGhosts = new Vector<GHOST>();
		for (GHOST ghost : GHOST.values()) {	
			if(!game.isGhostEdible(ghost) && game.getGhostLairTime(ghost) <= 0)
				activeGhosts.add(ghost);
		}
		return activeGhosts;
	}

	private boolean isCheckMate(Game g) {		
		// Si el pacman está más cerca de la Power Pill que los fantasmas no hay jaque
		if (isPacManCloserToPowerPill(g, 20000)) {
			return false;
		}

		Set<interseccion> visitadas = new HashSet<interseccion>();
		// rellenamos un array con los nodos de los fantasmas
		Vector<GHOST> ghosts = activeGhosts(g);
		if(ghosts.isEmpty())
			return false;

		Vector<Integer> nodosFijos = new Vector<Integer>();

		interseccion[] aux = new interseccion[6];
		rellenarProxDestinos(visitadas, nodosFijos, g);
		
		if(!visitadas.isEmpty())
			visitadas.toArray(aux);
		
		int i = 0;
		while (ghosts.size() > 1 && visitadas.size() > 0 && ghosts.size() - visitadas.size() >= 0) {
			if(aux[i] == null)
				break;
			GHOSTANDDISTANCE gyd = closestGhostToIntersection(g, aux[i], ghosts);
			if (gyd.distance < g.getDistance(g.getPacmanCurrentNodeIndex(), aux[i].identificador, g.getPacmanLastMoveMade(), DM.PATH)) {
				moves.put(gyd.ghost, g.getApproximateNextMoveTowardsTarget(g.getGhostCurrentNodeIndex(gyd.ghost), aux[i].identificador, g.getGhostLastMoveMade(gyd.ghost), DM.PATH));
				ghosts.remove(gyd.ghost);
				nodosFijos.add(g.getGhostCurrentNodeIndex(gyd.ghost));
				i++;
			}
			else {
				rellenarProxDestinos(visitadas, nodosFijos, g);
				visitadas.toArray(aux);
				i = 0;
			}			
		}
		return (visitadas.size() == 0);
	}

	private EnumMap<GHOST, Roles> getRoles(Game game) {
		EnumMap<GHOST, Roles> roles = new EnumMap<GHOST, Roles>(GHOST.class);

		for (GHOST ghostType : GHOST.values()) {
			roles.put(ghostType, Roles.Perseguidor);	
		}
		if(game.getActivePowerPillsIndices().length == 0 || activeGhosts(game).size() <= 2)
			return roles;
		
		//El que esta más cerca de la powerpill, y no está atacando al pacman
		ClosestPowerPillAndDistance closest = new ClosestPowerPillAndDistance();
		GHOST camper = GHOST.BLINKY;
		for (GHOST ghostType : activeGhosts(game)) {	
			ClosestPowerPillAndDistance aux = getClosestPowerPillAndDistance(game, game.getGhostCurrentNodeIndex(ghostType), game.getGhostLastMoveMade(ghostType));
			if(aux.distance < closest.distance && game.getDistance(game.getGhostCurrentNodeIndex(ghostType), game.getPacmanCurrentNodeIndex(), DM.EUCLID) > CONSTANT_ROL_CAMPEADOR){
				closest = aux;
				camper = ghostType;
			}
		}
		roles.put(camper, Roles.Campeador);

		return roles;
	}

	private void getMovesByRoles(Game game, EnumMap<GHOST, Roles> roles) {
		for (GHOST ghostType : GHOST.values()) {
			if (!game.doesGhostRequireAction(ghostType)) { // Si no se tiene que mover
				continue;
			}
			if(interseccionActual == null){
				moves.put(ghostType, getMovePerseguidor(game, ghostType, null));
			}
				
			interseccion proximaInterseccionPacman = (checkLastMoveMade) ? interseccionActual : getInterseccion(interseccionActual.destinos.get(ultimoMovimientoReal));
			switch (roles.get(ghostType)) {
				case Perseguidor:
					moves.put(ghostType, getMovePerseguidor(game, ghostType, proximaInterseccionPacman));
					break;
				case Campeador:
					moves.put(ghostType, getMoveCampeador(game, ghostType, proximaInterseccionPacman, CONSTANT_CAMPEADOR_ERROR));
					break;
				default:
					moves.put(ghostType, MOVE.NEUTRAL); // Random si no se le ha asignado ningun rol
					break;
			}
		}
	}

	// Se dirige a la interseccion donde es mas probable que se dirija el PacMan y yo estoy más cerca
	// (No voy a la inmediata, porque si fuese jaque mate ya lo habria hecho en el metodo isCheckMate())
	// Si el pacman esta mas cerca de la pildora que cualquier fantasma en cierto rango, huyo
	private MOVE getMovePerseguidor(Game game, GHOST ghostType, interseccion proximaInterseccionPacman) {
		if (isPacManCloserToPowerPill(game, CONSTANT_LIMITE_HUIDA_PERSEGUIDOR) || game.isGhostEdible(ghostType))
			return getMoveRunAway(game, ghostType);

		if(proximaInterseccionPacman == null){
			return game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghostType), game.getPacmanCurrentNodeIndex(),
				game.getGhostLastMoveMade(ghostType), DM.PATH);
		}

		// Mirar el camino con mas valor para el PacMan y el mas cercano a mi
		float valorMasAlto = -2, valor = 0;
		Integer destino = 0;
		Integer posGhost = game.getGhostCurrentNodeIndex(ghostType);
		for (MOVE d : proximaInterseccionPacman.destinos.keySet()) {
			if(posGhost == proximaInterseccionPacman.destinos.get(d)){
				destino = proximaInterseccionPacman.identificador;
				break;
			}
			valor = (float) proximaInterseccionPacman.pills.get(d)
					/ (float) (proximaInterseccionPacman.distancias.get(d) + game.getDistance(game.getPacmanCurrentNodeIndex(), proximaInterseccionPacman.identificador, DM.PATH)); // Valor del camino para el PacMan
			double distanceG = game.getDistance(game.getGhostCurrentNodeIndex(ghostType),
					proximaInterseccionPacman.destinos.get(d), game.getGhostLastMoveMade(ghostType), DM.PATH); // Cercania del fantasma hacia ese nodo
			valor /= distanceG;
			for (GHOST g : destinosGhosts.keySet()) { // Si hay un fantasma que se dirige hacia ahí y llega antes, no voy ahí
				if (g != ghostType && destinosGhosts.get(g).identificador != destino
						&& distanceG < game.getDistance(game.getGhostCurrentNodeIndex(g),
								proximaInterseccionPacman.destinos.get(d), game.getGhostLastMoveMade(g), DM.PATH)) {
					valor--;
					break;
				}
			}
			if (valor > valorMasAlto) { // si el valor es mas alto o si ya hay un fantasma que llega antes ahí
				valorMasAlto = valor;
				destino = proximaInterseccionPacman.destinos.get(d);
			}
		}

		destinosGhosts.put(ghostType, getInterseccion(destino));

		return game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghostType), destino,
				game.getGhostLastMoveMade(ghostType), DM.PATH);
	}

	// haga lo que haga, siempre va a estar mas cerca de una pildora que MsPacMan, tratando de acercarse si es posible al Pacman
	// Si el pacman esta mas cerca de la pildora que cualquier fantasma, huyo alejandome de los otros fantamas
	private MOVE getMoveCampeador(Game game, GHOST ghostType, interseccion proximaInterseccionPacman, int error) {
		if (isPacManCloserToPowerPill(game, CONSTANT_LIMITE_HUIDA_CAMPEADOR) || game.isGhostEdible(ghostType)) {
			return getMoveRunAway(game, ghostType);
		}
		ClosestPowerPillAndDistance cppadToGhost = getClosestPowerPillAndDistance(game, game.getGhostCurrentNodeIndex(ghostType), game.getGhostLastMoveMade(ghostType));
		ClosestPowerPillAndDistance cppadToPacMan = getClosestPowerPillAndDistance(game, game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade());
		interseccion inter = getInterseccion(game.getGhostCurrentNodeIndex(ghostType));
		double greaterDistance = 0;

		double distanceToPowerPillPacMan = (cppadToGhost.powerpill == cppadToPacMan.powerpill) ? 
			cppadToPacMan.distance : game.getDistance(game.getPacmanCurrentNodeIndex(), cppadToGhost.powerpill, game.getPacmanLastMoveMade(), DM.PATH);
		
		MOVE bestMove = game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghostType), cppadToGhost.powerpill, game.getGhostLastMoveMade(ghostType), DM.PATH);
		if(inter != null) {		
			for (MOVE move : inter.distancias.keySet()) {
				if(move == game.getGhostLastMoveMade(ghostType)) //No valoro el movimiento OPOSITE
					continue;
				double aux = game.getDistance(inter.destinos.get(move), cppadToGhost.powerpill, DM.EUCLID) + inter.distancias.get(move) + 
						game.getDistance(game.getPacmanCurrentNodeIndex(), proximaInterseccionPacman.identificador, game.getPacmanLastMoveMade(), DM.PATH) + error; //Aproximacion
				if(aux > greaterDistance && aux < distanceToPowerPillPacMan){
					greaterDistance = aux;
					bestMove = move;
				}
			}
			destinosGhosts.put(ghostType, getInterseccion(inter.destinos.get(bestMove)));
		}
		return bestMove;
	}

	// movimiento para huir alejandose tambien de los otros fantasmas
	private MOVE getMoveRunAway(Game game, GHOST ghostType) {
		MOVE prohibido = game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghostType), game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(ghostType), DM.PATH);
		MOVE bestMove = game.getApproximateNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(ghostType), game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(ghostType), DM.PATH);
		interseccion inter = getInterseccion(game.getGhostCurrentNodeIndex(ghostType));
		int[] posGhosts = new int[3]; int i = 0;
		for(GHOST g : activeGhosts(game)){
			if(g == ghostType) continue;
			posGhosts[i] = game.getGhostCurrentNodeIndex(g);
			i++;
		}
		if(inter != null) {
			double furthest = 0;
			for (MOVE move : inter.destinos.keySet()) {
				if(move == prohibido)
					continue;
				double aux = game.getDistance(game.getGhostCurrentNodeIndex(ghostType), game.getClosestNodeIndexFromNodeIndex(game.getGhostCurrentNodeIndex(ghostType), posGhosts, DM.EUCLID), DM.EUCLID);
				if(aux > furthest) {
					furthest = aux;
					bestMove = move;
				}
			}
		}
		return bestMove;
	}

	class ClosestPowerPillAndDistance{
		int powerpill = 0;
		double distance =  Double.MAX_VALUE;
	}

	private ClosestPowerPillAndDistance getClosestPowerPillAndDistance(Game game, int pos, MOVE lastMoveMade){
		ClosestPowerPillAndDistance cpad = new ClosestPowerPillAndDistance();
		for (int currentPill : game.getActivePowerPillsIndices()) {
			double aux = game.getDistance(pos, currentPill, lastMoveMade, DM.PATH);
			if (aux < cpad.distance) {
				cpad.distance = aux;
				cpad.powerpill = currentPill;
			}
		}
		return cpad;
	}

	private boolean isPacManCloserToPowerPill(Game game, int limit) {
		ClosestPowerPillAndDistance cpad = getClosestPowerPillAndDistance(game, game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade());
		// Solo miro si esta en el rango del fairplay
		if (cpad.distance < limit) {
			for (GHOST ghostType : activeGhosts(game)) {
				double dist = game.getDistance(game.getGhostCurrentNodeIndex(ghostType), cpad.powerpill, game.getGhostLastMoveMade(ghostType), DM.PATH);
				if (dist < cpad.distance || dist > limit * CONSTANT_LIMITE_MULTIPLIER)
					return false;
			}
			return true;
		}
		return false;
	}
}
