package es.ucm.fdi.ici.c2021.practica4.grupo09.ghosts.actions;

import java.util.Vector;

import es.ucm.fdi.ici.c2021.practica4.grupo09.MapaInfoGhost;
import es.ucm.fdi.ici.c2021.practica4.grupo09.MapaInfoGhost.interseccion;
import es.ucm.fdi.ici.c2021.practica4.grupo09.ghosts.PositionAproximator;
import es.ucm.fdi.ici.fuzzy.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GoToActiveGhostAction implements Action {
//La idea es de la �ltima posici�n del resto de fantasmas (siempre que estemos relativamente seguros de que no est�n edibles) coger la m�s cercana
//siempre que sea lo suficientemente fiable y aproximar la posici�n del compa�ero que me va a proteger
	private MapaInfoGhost mymap;
	GHOST ghost;
	Vector<Double> ActiveGhostsPosAccuracies;
	Vector<MOVE> GhostsLastMoveKnown;
	Vector<Double> EdibleAccurracy;
	Vector<interseccion> LastGhostsKnownPositions;
	interseccion PacmanLastPosKnown;
	float PacmanPosAccuracy;

	float PACMAN_POS_ACCURACY_LIMIT = .7f;
	float POS_ACCURACY_LIMIT = .7f;
	float EDIBLE_ACCURACY_LIMIT = .3f;

	public GoToActiveGhostAction(GHOST ghost, MapaInfoGhost map, Vector<Double> Actives, Vector<Double> edibles,
			Vector<interseccion> LastPos, interseccion PacmanPos, float PacmanAccur, Vector<MOVE> lastMoves) {
		this.ghost = ghost;
		this.mymap = map;
		this.ActiveGhostsPosAccuracies = Actives;
		this.EdibleAccurracy = edibles;
		this.GhostsLastMoveKnown = lastMoves;
		this.LastGhostsKnownPositions = LastPos;
		this.PacmanLastPosKnown = PacmanPos;
		
		this.PacmanPosAccuracy = PacmanAccur;

	}

//	private EnumMap<GHOST,Float> activeGhosts(Game game) {
//		EnumMap<GHOST,Float> activeGhosts = new EnumMap<GHOST,Float>(GHOST.class);
//		for (GHOST ghost : GHOST.values()) {
//			if (!game.isGhostEdible(ghost) && game.getGhostLairTime(ghost) <= 0)
//				activeGhosts.add(ghost);
//		}
//		return activeGhosts;
//	}

	private class DistanceAndGhost {
		int ghostIndex;
		double distance;

		public DistanceAndGhost(double d, int i) {
			ghostIndex = i;
			distance = d;
		}
	}

	private DistanceAndGhost nearestGhostDistance(MOVE m, Game game) {

		double nearestDist = Double.MAX_VALUE;
		// TENEMOS EN CUENTA LOS PARAMETROS FUZZY
		int index = 0;
		for (int i = 0; i < 4; i++) {
			if(GHOST.values()[i]==ghost)
				continue;
			////Si la seguridad de que el fantasma que buscamos es edible es baja es que no es edible muy probablemente, ya 
			//que esta variable fuzzy bajar� muy r�pidamente(el tiempo durante el que se est� edible es corto)
			if (LastGhostsKnownPositions.elementAt(i)!=null
					&& ActiveGhostsPosAccuracies.elementAt(i) >= POS_ACCURACY_LIMIT
					&& this.EdibleAccurracy.elementAt(i) < EDIBLE_ACCURACY_LIMIT) { 
				double aux = game.getDistance(game.getGhostCurrentNodeIndex(ghost),
						LastGhostsKnownPositions.elementAt(i).identificador, m, DM.PATH);
				if (aux < nearestDist) {
					nearestDist = aux;
					index = i;
				}
			}
		}
		return new DistanceAndGhost(nearestDist, index);
	}

	@Override
	public MOVE execute(Game game) {
		if (!game.doesGhostRequireAction(ghost))  //if does not require an action	
			return MOVE.NEUTRAL;

		MOVE best = MOVE.NEUTRAL;
		interseccion inter = mymap.getInterseccion(game.getGhostCurrentNodeIndex(ghost));
		double nearest = 100000;
		if (inter != null) {
			MOVE prohibido = MOVE.NEUTRAL;
			int selectedGhostIndex = -1;
			int myPos = game.getGhostCurrentNodeIndex(ghost);
			MOVE mylastMove = game.getGhostLastMoveMade(ghost);
			if (PacmanLastPosKnown !=null)
				prohibido = game.getApproximateNextMoveTowardsTarget(myPos, PacmanLastPosKnown.identificador, mylastMove, DM.EUCLID);
			// elegimos el fantasma m�s cercano buscando en todas direcciones excepto
			// en la prohibida
			for (MOVE move : inter.destinos.keySet()) {
				// si tenemos relativamente claro que el pacman est� en esa direcci�n (la variable prohibido es suficientemente fiable) no
				// buscamos en esa direcci�n 
				if (prohibido != MOVE.NEUTRAL && move == prohibido && this.PacmanPosAccuracy >= PACMAN_POS_ACCURACY_LIMIT)
					continue;
				DistanceAndGhost aux = nearestGhostDistance(move, game);
				if (aux.distance < nearest && aux.ghostIndex != ghost.ordinal()){
					nearest = aux.distance;
					best = move;
					selectedGhostIndex = aux.ghostIndex;

				}
			}
			// si la b�squeda ha dado al�n resultado significativo aproximamos la posici�n.
			// Si no da igual el movimiento
			// porque no tenemos informaci�n suficiente para decidir
			if (nearest < Double.MAX_VALUE && selectedGhostIndex > -1) {
				PositionAproximator aprox = new PositionAproximator(mymap, game, myPos, mylastMove,
						this.LastGhostsKnownPositions.elementAt(selectedGhostIndex),
						GhostsLastMoveKnown.elementAt(selectedGhostIndex),
						ActiveGhostsPosAccuracies.elementAt(selectedGhostIndex));
				best = aprox.getBestMoveTowardsEntityPos();
			}
		}
		return best;

	}
}
